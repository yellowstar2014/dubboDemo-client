package dubbo.client.PpcClient;

import dubbo.client.PpcClient.proxyHandler.RemotInvocationHandler;
import dubbo.client.registry.IServiceDiscovery;
import java.lang.reflect.Proxy;

/**动态代理类核心功能就是根据服务url地址去远程调用服务，并返回结果
 * @author yellow
 * @date 2019/9/18 17:22
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class RpcClientProxy {

    private IServiceDiscovery serviceDiscovery;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    //一定要是通用 interfaceClass   IDemoService
    public <T> T create(final Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, new RemotInvocationHandler(serviceDiscovery,interfaceClass));

    }
}
