package dubbo.client.RpcClient.proxyHandler;

import dubbo.client.RpcClient.RpcProxyHandler;
import dubbo.client.registry.IServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import com.dubbo.api.bean.RpcRequest;

/**
 * @author yellow
 * @date 2019/9/12 17:32
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class RemotInvocationHandler implements InvocationHandler {

    private IServiceDiscovery serviceDiscovery;
    private Class<?> interfaceClass;

    public RemotInvocationHandler(IServiceDiscovery serviceDiscovery,Class<?> interfaceClass) {
        this.serviceDiscovery = serviceDiscovery;
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //封装RpcRequest对象
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setTypes(method.getParameterTypes());
        request.setParams(args);
        request.setMethodName(method.getName());

        //服务发现， 因为接下来需要进行通信
        String serivceName = interfaceClass.getName();
        //获取服务url 地址,只获取一个合适的服务url
        String serviceAddress = serviceDiscovery.discover(serivceName);
        //解析host ip和port
        String[] arrs = serviceAddress.split(":");
        String host = arrs[0];
        int port = Integer.parseInt(arrs[1]);

        final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler(request);
        //通过netty的方式进行连接和发送数据
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch)throws Exception{
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            //使用netty写到最后就是写Handler的代码,用于处理业务（如服务端返回的数据，进行处理）
                            pipeline.addLast(rpcProxyHandler);
                        }

                    });

            //连接服务地址, connect：发起异步连接操作，调用同步方法 sync 等待连接成功
            ChannelFuture future = bootstrap.connect(host,port).sync();
            System.out.println("客户端向服务端"+host+":"+port+"发起异步连接请求..........");
            ///等待客户端链路关闭
            future.channel().closeFuture().sync();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }

        return rpcProxyHandler.getResponse();
    }
}
