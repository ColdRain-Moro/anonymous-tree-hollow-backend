# AnonymousTreeHollow

> 高校联合树洞 - 去中心化后台

一个后端接受到用户请求，将其推送到其他后端。保持分布式网络数据同步。

业余后端，欢迎贡献！

因为没有思路如何实现多个后端间的数据交换，去中心化功能暂时搁置

application.yml请自行配置

~~~yaml
spring:
  mail:
    host: "smtp.exmail.qq.com"
    port: 465
    username: ""
    password: ""
    protocol: "smtp"
  datasource:
    url: ""
    driver-class-name: "com.mysql.cj.jdbc.Driver"
    username: "root"
    password: ""

# Sa-Token配置
sa-token:
  # token 名称 (同时也是cookie名称)
  token-name: user-token
  # token 有效期，单位s 默认30天, -1代表永不过期
  timeout: 2592000
  # token 临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
  activity-timeout: -1
  # 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
  is-concurrent: true
  # 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
  is-share: false
  # token风格
  token-style: uuid
  # 是否输出操作日志
  is-log: false
  jwt-secret-key: "REDROCKREDROCK"

anonymous-tree-hollow:
  # 站点名称
  site-name: "重庆邮电大学"
  # 部署地址
  site-address: ""
  # 校园邮箱后缀
  stu-email-suffix: "@stu.cqupt.edu.cn"
  # 腾讯云cos相关配置
  cos:
    secret-id: ""
    secret-key: ""
    cos-region: ""
    bucket: ""
  # cdn不同文件存放路径
  cdn:
    image-bed-scope: "image_bed"
    id-card-scope: "id_card"
~~~