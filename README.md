[TOC]

# 流程

- 前端埋点
- 通过nginx到日志服务器
- 将 Event 打印到日志文件
- 将 Event 发送至Kafka

# 打包部署

- maven 打包
- 上传到服务器
- 启动

启动了三台，分别是 hd1:8081  hd2:8081  hd3:8081

# 配置 Nginx

- 在server内部配置
```lombok.config
location /applog{
    proxy_pass http://logserver.com;
}
```

- 在server外部配置反向代理

```lombok.config
upstream logserver.com{
    server hd1:8081 weight=1;
    server hd2:8081 weight=2;
    server hd3:8081 weight=3;
}
```

# 群启 群停 脚本

- 提前配置好java环境变量

```shell
#!/bin/bash

APPNAME=tmall-logger-0.0.1-SNAPSHOT.jar
case $1 in
    "start")
    {
        for i in hd1 hd2 hd3
        do
            echo "启动 logger 服务: $i"
            ssh $i  "java -Xms32m -Xmx64m  -jar /opt/module/tmall/$APPNAME >/dev/null 2>&1  &"
        done

        echo "启动 hd1 nginx"
        ssh hd1 "/opt/module/nginx/sbin/nginx"
    };;

    "stop")
    {
        echo "停止 hd1 nginx"
        sh hd1 "/opt/module/nginx/sbin/nginx -s stop"

        for i in hd1 hd2 hd3
        do
            echo "停止 logger 服务: $i"
            ssh $i "ps -ef|grep $APPNAME |grep -v grep|awk '{print \$2}'|xargs kill" >/dev/null 2>&1
        done
    };;
esac
```

