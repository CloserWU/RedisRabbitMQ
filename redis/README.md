## Redis配置详解

1. bind ：限定可访问IP地址

2. port ：redis启动端口

3. daemonize ：是否守护进程方式启动，在非守护进程方式下启动，可以显示一些有用的信息

4. pidfile ：指定守护线程启动的pid文件，最好根据端口来区分，如`/var/run/redis_6379.pid`

5. save \<time> \<action> ： 在time时间段(s)内，发生action次非重复set操作，将写回rdb

6. dbfilename ：指定rdb的文件，最好根据端口区分，如`dump_6379.rdb`。dbfilename can't be a path, just a filename

7. dir ：指定rdb文件的文件夹位置，aof的文件夹位置也根据这个。

   样例：若dir为`./` 则dump.rdb在当前文件夹下生成，每次新开启redis-server，也会在当前文件夹下找dump.rdb，当前文件夹，不管是在哪里启动redis，都是当前的文件夹下

8. replicaof \<ip> \<port> ：指定主库的ip和端口，此库将是slave库

9. masterauth ：若主库设置了密码，那么slave也要设置此密码

10. replica-serve-stale-data ：若主库down了，那么slave库是否还能读数据

11. replica-read-only slave：库是否只读

12. replica-priority ：重新选举master时，按此值来考虑优先级，值越低，优先级越高，默认100

13. requirepass ：数据库密码

14. appendonly ：是否开启aof

15. appendfilename ：在dir下的aof文件

16. appendfsync ：aof保存机制，按秒保存/每次操作保存/不保存

17. auto-aof-rewrite-min-size ：保存的aof文件按此大小分割 （尚不准确）



## Sentinel配置详解

1. port：端口

2. daemonize：是否守护线程启动

3. pidfile：pid文件全地址，按端口区分

4. dir：工作目录

5. sentinel monitor \<mastername> \<masterip> \<masterport> \<quorm> ：哨兵监控的master节点，quorm是指投票选举新master时需要几个哨兵同意，一般设置为N/2+1(N为哨兵总数)。

   >2 个哨兵，quorm=2
   >3 个哨兵，quorm=2
   >4 个哨兵，quorm=2
   >5 个哨兵，quorm=3
   >...

   <span style="color:red">注意： 哨兵最少两个，否则无法选举。哨兵最好是奇数个。</span>

6. sentinel down-after-milliseconds \<mastername> \<mills>：master节点down后，经过多少毫秒开启重新选举。此时认为master是主观下线。

7. sentinel failover-timeout \<mastername> \<mills>：master节点down后，经过多少毫秒认为是客观下线。

8. sentinel auth-pass \<mastername>123456：master的密码



## 常用操作

```shell
redis-server ./redis_6379.conf
redis-cli -p 6379
>>> keys *
>>> info replitaion
>>> auth 123456
>>> shutdown
```

## 细节

1. redis的安装

   ```shell
   yum -y install gcc automake autoconf libtool make
   yum install gcc-c++
   wget http://download.redis.io/releases/redis-5.0.7.tar.gz
   tar xzf redis-5.0.7.tar.gz
   cd redis-5.0.7
   (make distclean)
   make
   make install  
   (usr/local/bin)
   ```

2. 主从问题

   当一个节点成为master节点的slave后，将会发送sync到master，Master接到命令启动后台的存盘进程，同时收集所有接收到的用于修改数据集命令，在后台进程执行完毕之后，master将传送整个数据文件到slave,以完成一次**完全**同步。意思就是，只要slave关系建立，master中的全部信息slave都能获取到，以后新的信息也能获取到。

3. rdb和aof的区别

   ​	rdb是创建一个新的线程，来专门处理IO持久化。每次redis执行操作，rdb会将数据写入到临时文件(内存中)，等到达save标准后，将其写入磁盘，在这个过程中，主进程不进行IO，所以rdb效率很高。缺点是最后一次待写入磁盘的信息可能丢失。

   ​	aof是根据配置，按规则直接写入磁盘文件，将redis执行的所有操作都记录成日志。是文件追加的方式。AOF采用文件追加方式，文件会越来越大为避免出现此种情况，新增了重写机制,当AOF文件的大小超过所设定的阈值时，Redis就会启动AOF文件的内容压缩，只保留可以恢复数据的最小指令集.可以使用命令bgrewriteaof。

   ​	AOF文件持续增长而过大时，会fork出一条新进程来将文件重写(也是先写临时文件最后再rename)，遍历新进程的内存中数据，每条记录有一条的Set语句。重写aof文件的操作，并没有读取旧的aof文件，而是将整个内存中的数据库内容用命令的方式重写了一个新的aof文件，这点和快照有点类似。

   ​	Redis会记录上次重写时的AOF大小，默认配置是当AOF文件大小是上次rewrite后大小的一倍且文件大于64M时触发。在文件大于64m前，不会触发rewrite。