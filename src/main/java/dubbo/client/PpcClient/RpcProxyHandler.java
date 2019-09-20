package dubbo.client.PpcClient;

import com.dubbo.api.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**业务处理,数据读写
 * @author yellow
 * @date 2019/9/19 16:28
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

    private RpcRequest request;
    private Object response;
    public Object getResponse(){return response;}
    public void setResponse(Object res){
        this.response=res;
    }

    public RpcProxyHandler(RpcRequest request) {
        this.request = request;
    }
    /**
     * 当客户端和服务端 TCP 链路建立成功之后，Netty 的 NIO 线程会调用 channelActive 方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        String reqMsg = "我是客户端 " + Thread.currentThread().getName();
//        System.out.println("@@@@reqMsg:"+reqMsg);
//        byte[] reqMsgByte = reqMsg.getBytes("UTF-8");
//        ByteBuf reqByteBuf = Unpooled.buffer(reqMsgByte.length);
//        /**
//         * writeBytes：将指定的源数组的数据传输到缓冲区
//         * 调用 ChannelHandlerContext 的 writeAndFlush 方法将消息发送给服务器
//         */
//        reqByteBuf.writeBytes(reqMsgByte);
     //   ctx.writeAndFlush(reqByteBuf);
        System.out.println("向服务端发起请求调用的接口是："+request.toString());
        ctx.writeAndFlush(request);
    }

    /**
     *
     * @param ctx 可以用来向服务端发送数据
     * @param msg 接收到服务端发来的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接收到服务端调用接口后返回的结果数据是："+msg.toString());
        setResponse(msg);

    }

    /**
     * 当发生异常时，打印异常 日志，释放客户端资源
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**释放资源*/
        System.out.println("Unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }
}
