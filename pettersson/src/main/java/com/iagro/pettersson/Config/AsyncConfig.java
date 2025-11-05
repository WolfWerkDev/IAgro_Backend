package com.iagro.pettersson.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "rtExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);         // número base de hilos
        executor.setMaxPoolSize(30);          // máximo de hilos simultáneos
        executor.setQueueCapacity(100);       // cola antes de crear nuevos hilos
        executor.setThreadNamePrefix("RT-Worker-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "reportExecutor")
    public Executor reportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // pocos hilos
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Report-Worker-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "iaExecutor")
    public Executor iaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Número base de hilos para llamadas concurrentes normales
        executor.setCorePoolSize(10);

        // Máximo de hilos que se pueden crear en picos de carga
        executor.setMaxPoolSize(30);

        // Capacidad de la cola para solicitudes en espera
        executor.setQueueCapacity(200);

        // Prefijo del nombre de los hilos (útil para logs y debugging)
        executor.setThreadNamePrefix("IA-Worker-");

        // Esperar a que terminen las tareas antes de apagar el servidor
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);

        executor.initialize();
        return executor;
    }
}
