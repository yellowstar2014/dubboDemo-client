package dubbo.client.loadBalance;

import java.util.List;
import java.util.Random;

/**负载均衡算法
 * @author yellow
 * @date 2019/9/18 17:01
 * 温馨提醒:
 * 代码千万行，
 * 注释第一行。
 * 命名不规范，
 * 同事两行泪。
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> repos) {
        int len = repos.size();
        Random random = new Random();
        return  repos.get(random.nextInt(len));
    }
}
