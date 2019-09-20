package dubbo.client.loadBalance;

import java.util.List;

/**负载均衡算法
 * @author yellow
 * @date 2019/9/18 16:59
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public interface LoadBalance {
    /**
     *
     * @param repos   服务url集合
     * @return 返回一个合适的url
     */
    String select(List<String> repos);
}
