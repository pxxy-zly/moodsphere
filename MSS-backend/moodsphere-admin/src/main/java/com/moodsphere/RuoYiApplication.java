package com.moodsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(RuoYiApplication.class, args);
        System.out.println("心境气象站启动成功\n" +
                " __  __   ____    ____  \n" +
                "|  \\/  | / ___|  / ___| \n" +
                "| |\\/| | \\___ \\  \\___ \\ \n" +
                "| |  | |  ___) |  ___) |\n" +
                "|_|  |_| |____/  |____/ \n");
    }
}
