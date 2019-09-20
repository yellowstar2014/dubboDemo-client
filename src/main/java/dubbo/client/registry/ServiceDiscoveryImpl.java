package dubbo.client.registry;

import dubbo.client.loadBalance.LoadBalance;
import dubbo.client.loadBalance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**服务发现类，从zk上去发现需要的服务url
 * @author yellow
 * @date 2019/9/18 16:48
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class ServiceDiscoveryImpl implements IServiceDiscovery {

    List<String> repos = new ArrayList<>();

    private CuratorFramework curatorFramework;

    public ServiceDiscoveryImpl(){
        //根据ZkConfig 中的字符串初始化curatorFramework
        curatorFramework= CuratorFrameworkFactory.builder().
                connectString(ZkConfig.CONNECTION_STR).sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(1000,10)).build();
        curatorFramework.start();
    }

    @Override
    public String discover(String serviceName) {
        //   /registrys/com.demo.IDemoService
        String servicePath=ZkConfig.ZK_REGISTER_PATH+"/"+serviceName;

        try {
            //根据服务名称返回list集合，集合元素是url
            repos = curatorFramework.getChildren().forPath(servicePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //订阅的实现
        registerWatch(servicePath);
        //客户端的url负载均衡算法
        LoadBalance loadBalance = new RandomLoadBalance();

        return loadBalance.select(repos);
    }

    /**
     * zk的订阅功能
     * @param servicePath
     */
    private void registerWatch(final String servicePath){
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework,servicePath,true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                repos=curatorFramework.getChildren().forPath(servicePath);
            }
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            childrenCache.start();
        }catch (Exception ex){
            throw new RuntimeException("注册PathChild watcher 异常"+ex);
        }
    }
}
