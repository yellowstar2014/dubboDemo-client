package dubbo.client.registry;

/**
 * @author yellow
 * @date 2019/9/18 16:45
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public interface IServiceDiscovery {
    //根据服务名称com.demo.XXX  从注册中心获取 ---url地址  127.0.0.1:8080
    String discover(String serviceName);
}
