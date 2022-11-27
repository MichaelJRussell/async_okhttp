package mjr.async_okhttp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class AsyncOkhttpApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncOkhttpApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		var executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("AsyncOkhttp-");
		executor.initialize();

		return executor;
	}
}
