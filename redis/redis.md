```shell
yum -y install gcc automake autoconf libtool make
yum install gcc-c++
```



```shell
wget http://download.redis.io/releases/redis-5.0.7.tar.gz
tar xzf redis-5.0.7.tar.gz
cd redis-5.0.7
make
```



```shell
$ src/redis-server ./redis.conf
$ src/redis-server
$ src/redis-cli -p 6379
$ src/redis-check-aof  --fix appendonly.aof 
```



```shell
aof 按秒备份  最少64m的重写文件，超过之后每次大小翻倍就重写
rdb 按conf规则备份
```



```shell
cli>>>config get requirepass
cli>>>config set requirepass ""
cli>>>config get dir
cli>>>auto password123456
cli>>>save (dump.rdb立即)

// 事务
cli>>>multi
cli>>>set k1 v1  
cli<<<QUENE
cli>>>set k2 v2
cli<<<QUENE
cli>>>exec

cli>>>multi
cli>>>set k1 v2  
cli<<<QUENE
cli>>>set k2 v1
cli<<<QUENE
cli>>>discard


cli>>>multi
cli>>>set k1 v1  
cli<<<QUENE
cli>>>getset k2  (错误命令，事务失败) (语法错误)
cli<<<ERROR
cli>>>exec
cli<<<ERROR

cli>>>multi
cli>>>set k1 v1  
cli<<<QUENE
cli>>>incr k1  (类型不匹配，但命令正确) (执行错误)
cli<<<QUENE
cli>>>set k4 v4
cli<<<QUENE
cli>>>get k4
cli<<<QUENE
cli>>>exec
cli<<<... (正确命令正确执行，执行错误的命令不会执行。k1的值不会变，k4顺利set)

```



```shell
cli>>>set balance 100
cli>>>set debt 0
cli>>>watch balance
cli>>>multi
cli>>>decrby balance 20
cli>>>incrby debt 20
cli>>>exec  (right)
cli>>>get balance
cli<<<80

cli>>>watch balance
cli>>>set balance 800
cli>>>multi
cli>>>decrby balance 20
cli>>>incrby debt 20
cli>>>exec  (wrong)
cli<<<nil
cli>>>get balance
cli<<<800


cli>>>watch balance
cli>>>set balance 500
cli>>>unwatch balance
cli>>>watch balance
cli>>>set balance 80
cli>>>decrby balance 20
cli>>>incrby debt 20
cli>>>exec  (right)
cli>>>get balance
cli<<<80
```



```shell
// 主从复制
$ vim redis.conf
--- port "xx"
--- pidfile "xx"
--- dbfilename "xx"
--- masterauth 123456 (若主机有密码， 则此项必填)
--- slaveof ip port (可在配置文件中直接配置)
从机只读

cli>>>info replication
cli>>>slaveof no one (转为主库)
cli>>>slaveof 127.0.0.1 6379
// 一旦备建立主从关系，从机将备份主机全部kv


- master shutdown后，slave还是slave，状态变为down
  master 重启后，slave将依旧以原master为主，进行replication
- slave shutdown后，再重启将变为master。除非写进配置文件。

sentinel.conf
哨兵的配置文件中需加入密码认证
哨兵开启时，会记录redis集群状态，即使redis集群全部挂掉，只要哨兵还在，那么reids集群恢复后，还会回到原来的主从状态。
当哨兵down后，重启reids集群，不做任何修改，当哨兵开启，redis集群也会自动恢复原有主从状态
```

