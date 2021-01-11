import json, flask
from flask import request, Markup
import pymysql
from sshtunnel import SSHTunnelForwarder
from flask_cors import CORS

# # 使用ssh远程连接云服务器
# server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
#                             ssh_username="root",
#                             ssh_password="4p6DxcEy9PPu@K*",
#                             remote_bind_address=("localhost", 3306))
# server.start()
#
# print(server.local_bind_port)
#
# # host必须是127.0.0.1
# db = pymysql.connect(host='127.0.0.1',
#                      port=server.local_bind_port,
#                      user='dongqiudi',
#                      password='dqdleo',
#                      database='dongqiudi')
#
# cursor = db.cursor()

app = flask.Flask(__name__)
# 解决跨域问题
CORS(app, resources=r'/*')

# 提交评论
# 方法：GET
# 参数：newsID 新闻id
#       userID 用户id
#       content 内容
# 返回：成功
@app.route('/submitcomment', methods=['get'])
def submit():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()

    newsID = request.values.get('newsid')
    userName = request.values.get('username')
    content = request.values.get('content')
    sql = 'INSERT INTO `comment`(`NEWS_ID`, `USER_NAME`, `CONTENT`, `TIME`) VALUES (\'%s\',\'%s\',\'%s\',now())' % (
        newsID, userName, content)
    print(sql)
    cursor.execute(sql)
    db.commit()

    cursor.close()
    db.close()
    server.close()
    return '成功'

# 通过新闻获得评论
# 方法：GET
# 参数：ID 新闻id
# 返回：json
@app.route('/getcomments', methods=['get'])
def getComments():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()

    newsID = request.values.get('news')
    sql = 'SELECT * FROM `comment` WHERE NEWS_ID=\'%s\'' % newsID
    cursor.execute(sql)
    datas = cursor.fetchall()
    results = []
    for data in datas:
        # userID = data[2]
        # sql = 'SELECT NAME FROM `user` WHERE USER_ID=\'%s\'' % userID
        # cursor.execute(sql)
        # userName = cursor.fetchall()
        # userName = userName[0][0]
        dict = {'姓名': data[2], '内容': data[3], '时间': str(data[4])}
        results.append(dict)
    print(results)

    cursor.close()
    db.close()
    server.close()
    return json.dumps(results, ensure_ascii=False)


# 通过标签获取新闻列表
# 方法：GET
# 参数：tag 标签名
# 返回：json
@app.route('/getnewsbytag', methods=['get'])
def getNewsByTag():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()

    tag = request.values.get('tag')
    sql = 'SELECT NEWS_ID FROM `news_tag` WHERE TAG=\'%s\'' % tag
    cursor.execute(sql)
    datas = cursor.fetchall()
    results = []
    for data in datas:
        sql = 'SELECT * FROM `news` WHERE NEWS_ID=\'%s\'' % data[0]
        cursor.execute(sql)
        newsGeted = cursor.fetchall()
        dict = {'标题': newsGeted[0][1], '内容': newsGeted[0][2], '时间': str(newsGeted[0][3]), '封面': newsGeted[0][4],
                'ID': newsGeted[0][0]}
        results.append(dict)
    print(results)
    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return json.dumps(results, ensure_ascii=False)


# 获取新闻列表
# 方法：GET
# 参数：无
# 返回：json
@app.route('/getnews', methods=['get'])
def getNews():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()
    sql = "select * from news"
    cursor.execute(sql)
    datas = cursor.fetchall()
    results = []
    for data in datas:
        dict = {'标题': data[1], '内容': data[2], '时间': str(data[3]), '封面': data[4], 'ID': data[0]}
        results.append(dict)
    print(results)
    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return json.dumps(results, ensure_ascii=False)


# 获取比赛列表
# 方法：GET
# 参数：无
# 返回：json
@app.route('/getmatches', methods=['get'])
def getMatches():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()

    sql = "select * from matches"
    cursor.execute(sql)
    datas = cursor.fetchall()
    results = []
    for data in datas:
        dict = {'日期': data[1], '时间': data[2], '主队': data[3], '客队': data[4], '比分': data[5], '联赛': data[6]}
        # print(dict)
        results.append(dict)
    print(results)
    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return json.dumps(results, ensure_ascii=False)


# 发布新闻
# 方法：POST
# 参数：无
# 返回：‘成功’
@app.route('/publish', methods=['post', 'get'])
def publish():
    print(request.headers)
    print(request.form)
    print(request.form['new_content'])
    print(type(request.form['new_content']))
    file = open('/www/wwwroot/8.129.27.254/news/' + request.form['new_title'] + '.html', 'w', encoding='utf-8')
    file.write(request.form['new_content'])
    print(file)
    file.close()

    # 插入数据库
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()
    news_title = request.form['new_title']
    news_path = news_title + '.html'
    news_cover = request.form['cover']
    sql = "INSERT INTO `news`(`NEWS_TITLE`, `NEWS_CONTENT`, `PUBLISH_DATA`,`COVER`) VALUES (\'%s\',\'%s\',now(),\'%s\')" % (
        news_title, news_path, news_cover)
    print(sql)
    print(news_path)
    cursor.execute(sql)
    db.commit()

    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return '成功'


# 用户注册
# 方法：GET
# 参数：accont:用户账号
#       pwd：用户密码
#       name：用户名称
# 返回：‘账户已存在’
#       '用户名已使用'
#       '成功'
@app.route('/register')
def register():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()

    accont = request.values.get('accont')
    password = request.values.get('pwd')
    name = request.values.get('name')
    print(accont, password, name)
    sql = "select * from user where ACCONT = \'%s\'" % accont
    cursor.execute(sql)
    print(sql)
    data = cursor.fetchall()
    if data != ():
        return '账户已存在'
    else:
        sql = "select * from user where NAME=\'%s\'" % name
        print(sql)
        cursor.execute(sql)
        data = cursor.fetchall()
        if data != ():
            return '用户名已使用'
        else:
            sql = "INSERT INTO `user`(`ACCONT`, `PASSWORD`, `NAME`, `CREATE_DATE`) VALUES (\'%s\',\'%s\',\'%s\',now())" % (
                accont, password, name)
            print(sql)
            cursor.execute(sql)
            db.commit()
            print(cursor.fetchall())
            return '成功'

    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()


# 判断账户密码
# 方法：GET
# 参数：username:账户
#       pwd：密码
# 返回：'用户不存在'
#       '密码错误'
#       '密码正确'
@app.route('/checkuser')
def checkuser():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()

    userName = request.values.get('username')
    password = request.values.get('pwd')
    sql = "select PASSWORD from user where ACCONT =\'%s\'" % userName
    cursor.execute(sql)
    realPwd = cursor.fetchall()
    print(realPwd)
    if realPwd == ():
        return '用户不存在'
    else:
        realPwd = realPwd[0][0]
        print(realPwd)
        if realPwd != password:
            return '密码错误'
        elif realPwd == password:
            sql = "select NAME from user where ACCONT=\'%s\'" % userName
            cursor.execute(sql)
            userNick = cursor.fetchall()
            userNick = userNick[0][0]
            print(userNick)
            return userNick
        else:
            return '123'

    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()


# 查询排行榜
# 方法：GET
# 参数：tablename:表名
# 返回：json
@app.route('/queryrank')
def queryrank():
    # 获取请求
    # 使用ssh远程连接云服务器
    server = SSHTunnelForwarder(ssh_address_or_host=("8.129.27.254", 22),
                                ssh_username="root",
                                ssh_password="4p6DxcEy9PPu@K*",
                                remote_bind_address=("localhost", 3306))
    server.start()

    print(server.local_bind_port)

    # host必须是127.0.0.1
    db = pymysql.connect(host='127.0.0.1',
                         port=server.local_bind_port,
                         user='dongqiudi',
                         password='dqdleo',
                         database='dongqiudi')

    cursor = db.cursor()

    table_name = request.values.get('tablename')
    sql = ""

    if table_name:
        if table_name == 'yingchao':
            sql = 'select * from yingchao_ranking'

        elif table_name == 'yijia':
            sql = 'select * from yijia_ranking'

        elif table_name == 'xijia':
            sql = 'select * from xijia_ranking'

        elif table_name == 'dejia':
            sql = 'select * from dejia_ranking'

        elif table_name == 'zhongchao':
            sql = 'select * from zhongchao_ranking'

        cursor.execute(sql)
        datas = cursor.fetchall()
        results = []
        for data in datas:
            team_id = data[1]
            sql = 'select NAME, TEAM_PIC from team where TEAM_ID =\'%s\'' % team_id
            cursor.execute(sql)
            datas = cursor.fetchall()
            # print(datas)
            result = {'排名': data[0], '队标': datas[0][1], '球队名': datas[0][0],
                      '场次': data[2], '胜': data[3],
                      '平': data[4], '负': data[5],
                      '进球': data[6], '失球': data[7],
                      '净胜球': data[8], '积分': data[9]}
            results.append(result)
        # 关闭数据库连接
        cursor.close()
        db.close()
        server.close()
        return json.dumps(results, ensure_ascii=False)


    else:
        resu = {'code': 1001, 'message': '参数不能为空'}
        # 关闭数据库连接
        cursor.close()
        db.close()
        server.close()
        return json.dumps(resu, ensure_ascii=False)


@app.route('/index')
def index():
    return 'hello'


@app.route('/hello')
def hello():
    return 'hello world!'


@app.route('/goodbye')
def goodbye():
    return 'goodbye!'


if __name__ == '__main__':
    # 上传服务器时要将host改成0.0.0.0
    # 调试的时候host使用127.0.0.1
    app.run(debug=True, port=8000, host='0.0.0.0')
