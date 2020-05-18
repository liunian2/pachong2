#第一次push需要网址：
#git add --all
#git commit -m "提交信息"
#git remote add origin '远程仓库url'
#git push -u origin 对应远程分支名

# git命令
#  git add .
#  git status
#  git commit -m "备注"
#  git push
#  git pull 从远程拉取代码

#springboot jar 启动后台挂载
#nohub java -jar pachong.jar

#数据库设计
```sql
CREATE TABLE crawler(
    ID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    title VARCHAR(255),
    time date ,
    contextUrl VARCHAR(255),
    content VARCHAR(2550),
    attchmentsUrl VARCHAR(2550),
    description VARCHAR(2550),
    ower VARCHAR(255),
    createTime date 
```