package dubbo.client;

import dubbo.client.RpcClient.RpcClientProxy;
import dubbo.client.registry.IServiceDiscovery;
import dubbo.client.registry.ServiceDiscoveryImpl;
import com.dubbo.api.IDemoService;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        IServiceDiscovery serviceDiscovery=new ServiceDiscoveryImpl();//服务发现类
        //服务发现  放在动态代理中进行了 Netty
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);
        IDemoService iDemoService = rpcClientProxy.create(IDemoService.class);//远程通信

        System.out.println( iDemoService.sayHello("yellow"));//远程调用的方式， 调用的是服务端的方法
    }
}
