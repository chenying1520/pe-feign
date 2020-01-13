package feign.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import feign.Feign;
import feign.Target;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 用来控制 hystrix 运行时的参数配置，可自定义
 *
 * @author ying.chen2
 * @date 2020/1/13
 */
public interface InvocationRuntimeSetterFactory {

  /**
   * 创建 hystrix Setter
   * @param target
   * @param proxy
   * @param method
   * @param args
   * @return
   * @thows
   * @author ying.chen2
   * @date 2020/1/13
   */
  HystrixCommand.Setter create(final Target<?> target, final Object proxy, final Method method, final Object[] args);


  final class Default implements InvocationRuntimeSetterFactory {

    @Override
    public HystrixCommand.Setter create(final Target<?> target, final Object proxy, final Method method, final Object[] args) {
      String groupKey = target.name();
      String commandKey = Feign.configKey(target.type(), method);

      Optional<Object> anyUri = Stream.of(args).findAny();
      if (anyUri.isPresent()) {
        URI uri = (URI) anyUri.get();
        groupKey = groupKey + "#" + uri.toString();
        commandKey = commandKey + "#" + uri.toString();
      }
      HystrixCommand.Setter setter = HystrixCommand.Setter
              .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
              .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));

      return setter;
    }
  }
}