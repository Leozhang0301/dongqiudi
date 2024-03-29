import json, flask
from flask import request, Markup
import pymysql
from sshtunnel import SSHTunnelForwarder
from flask_cors import CORS

import os
import pandas as pd
from sklearn.preprocessing import OneHotEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
import numpy as np

encoder = OneHotEncoder()
forest_clf = RandomForestClassifier(n_estimators=100, n_jobs=-1, min_samples_leaf=1)


# print(soc_data.head())


def getResult(data):
    # home team win
    if data['FT Team 1'] > data['FT Team 2']:
        return 1
    # home team loss
    elif data['FT Team 1'] < data['FT Team 2']:
        return -1
    # tie
    elif data['FT Team 1'] == data['FT Team 2']:
        return 0


def train():
    soc_data = pd.read_csv('soccer.csv')
    if 'result' in soc_data:
        soc_data.drop(columns=['result'], inplace=True)
    soc_data['result'] = soc_data.apply(getResult, axis=1)

    soc_data.drop(
        columns=['Round', 'Date', 'FT', 'HT', 'Year', 'Country', 'FT Team 1', 'FT Team 2', 'HT Team 1', 'HT Team 2',
                 'GGD',
                 'Team 1 (pts)', 'Team 2 (pts)'], inplace=True)

    y = soc_data['result']
    X = soc_data.drop(columns=['result'])

    X_tr = encoder.fit_transform(X)

    X_soc_train, X_soc_test, y_soc_train, y_soc_test = train_test_split(X_tr, y, test_size=0.2, random_state=42)

    forest_clf.fit(X_soc_train, y_soc_train)
    print('fit succsess')


train()

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


# 取消关注
# 方法：GET
# 参数：username 用户名
#       teamname 球队名
# 返回：string '成功'
@app.route('/disfollow', methods=['get'])
def disfollow():
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
    username = request.values.get('username')
    teamname = request.values.get('teamname')
    # 用username和teamname查询id
    sql = 'select USER_ID from user where name=\'%s\'' % username
    cursor.execute(sql)
    userid = cursor.fetchall()

    sql = 'select TEAM_ID from team where name=\'%s\'' % teamname
    cursor.execute(sql)
    teamid = cursor.fetchall()
    sql = 'delete from follow where team_id=%s and user_id=%s' % (teamid[0][0], userid[0][0])
    print(sql)
    cursor.execute(sql)
    db.commit()
    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return '成功'


# 添加关注
# 方法：GET
# 参数：username 用户名
#       teamname 球队名
# 返回：string '成功'/'已关注'
@app.route('/addfollow', methods=['get'])
def addFollow():
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

    username = request.values.get('username')
    teamname = request.values.get('teamname')
    sql = 'select * from user,follow,team where user.USER_ID=follow.USER_ID and team.TEAM_ID=follow.TEAM_ID and ' \
          'user.NAME=\'%s\' and team.NAME=\'%s\'' % (username, teamname)
    print(sql)
    cursor.execute(sql)
    result = cursor.fetchall()
    # 表中没有则添加
    if result == ():
        # 用username和teamname查询id
        sql = 'select USER_ID from user where name=\'%s\'' % username
        cursor.execute(sql)
        userid = cursor.fetchall()

        sql = 'select TEAM_ID from team where name=\'%s\'' % teamname
        cursor.execute(sql)
        teamid = cursor.fetchall()
        sql = 'insert into follow(team_id,user_id) values(%s,%s)' % (teamid[0][0], userid[0][0])
        cursor.execute(sql)
        db.commit()

        # 关闭数据库连接
        cursor.close()
        db.close()
        server.close()
        return '成功'
    # 表中有记录
    else:
        # 关闭数据库连接
        cursor.close()
        db.close()
        server.close()
        return '已关注'


# 查看用户权限 用于管理后台
# 方法：GET
# 参数：username 用户名
#       pwd 密码
# 返回：string '用户不存在'/'密码错误'/权限级别
@app.route('/checkmanageruser', methods=['get'])
def check():
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
            sql = "select ROOT from user where ACCONT=\'%s\'" % userName
            cursor.execute(sql)
            root = cursor.fetchall()
            root = str(root[0][0])
            print(root)
            return root
        else:
            return '123'

    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()


# 举报
# 方法: GET
# 参数：newsID 新闻id
#       userID 用户id
#       content 内容
# 返回：成功
@app.route('/submitreport', methods=['get'])
def submitReport():
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
    sql = 'select COMMENT_ID from comment where NEWS_ID=\'%s\' and USER_NAME=\'%s\' and CONTENT=\'%s\'' % (
        newsID, userName, content)
    cursor.execute(sql)
    commentID = cursor.fetchall()
    print(commentID[0][0])
    sql = 'insert into report(COMMENT_ID) values(%s) ' % commentID[0][0]
    cursor.execute(sql)
    db.commit()

    cursor.close()
    db.close()
    server.close()
    return '成功'


# 删除举报  举报不成功管理员不删除评论
# 只有举报表被删除
# 方法：GET
# 参数： id 举报id
# 返回：成功
@app.route('/cancelreport', methods=['GET'])
def cancelreport():
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
    report_id = request.values.get('id')

    sql = 'delete from report where report_id = %s' % report_id
    cursor.execute(sql)
    db.commit()

    cursor.close()
    db.close()
    server.close()
    return '成功'


# 删除评论  举报成功管理员删除评论
# 举报表和评论表都会删除
# 方法：GET
# 参数： id 举报id
# 返回：成功
@app.route('/deletecomment', methods=['GET'])
def deletecomment():
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
    report_id = request.values.get('id')

    sql = 'delete from comment where comment_id=(select comment_id from report where report_id =%s)' % report_id
    cursor.execute(sql)
    sql = 'delete from report where report_id = %s' % report_id
    cursor.execute(sql)

    db.commit()

    cursor.close()
    db.close()
    server.close()
    return '成功'


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
    sql = 'SELECT * FROM `comment` WHERE NEWS_ID=\'%s\' order by time desc' % newsID
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


# 获取举报列表 用于管理员页面
# 方法：GET
# 参数：无
# 返回：json
@app.route('/getreports', methods=['get'])
def getreports():
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

    sql = 'SELECT comment.USER_NAME,comment.CONTENT,comment.TIME,report.REPORT_ID,news.NEWS_TITLE FROM comment,report,' \
          'news WHERE report.COMMENT_ID=comment.COMMENT_ID AND comment.NEWS_ID=news.NEWS_ID '
    cursor.execute(sql)
    datas = cursor.fetchall()
    results = []
    for data in datas:
        dict = {'ID': data[3], '新闻标题': data[4], '评论内容': data[1], '用户名': data[0], '评论时间': str(data[2])}
        results.append(dict)
    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return json.dumps(results, ensure_ascii=False)


# 删除新闻 用于管理员页面
# 方法：GET
# 参数：id newsid
# 返回：成功
@app.route('/deletenews', methods=['get'])
def deletenews():
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
    newsid = request.values.get('id')
    sql = 'delete from news where NEWS_ID=%s' % newsid
    cursor.execute(sql)
    sql = 'delete from news_tag where NEWS_ID=%s' % newsid
    cursor.execute(sql)
    sql = 'delete from comment where NEWS_ID=%s' % newsid
    cursor.execute(sql)
    db.commit()

    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return '成功'


# 通过用户名获得新闻列表
# 方法：GET
# 参数：username 用户名
# 返回：json
@app.route('/getnewsbyusername', methods=['get'])
def getnewsbyusername():
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
    username = request.values.get('username')
    sql = 'SELECT team.NAME from team,follow,user WHERE team.TEAM_ID=follow.TEAM_ID AND follow.USER_ID=user.USER_ID ' \
          'AND ' \
          'user.NAME=\'%s\'' % username
    cursor.execute(sql)
    follows = cursor.fetchall()

    results = []
    # print(follows)
    for tag in follows:
        sql = 'SELECT NEWS_ID FROM `news_tag` WHERE TAG=\'%s\'' % tag
        cursor.execute(sql)
        datas = cursor.fetchall()
        sql = 'select * from news where news_id in ('
        if datas != ():
            i = 0
            for data in datas:
                # # print(data[0])
                # sql = 'SELECT * FROM `news` WHERE NEWS_ID=%s' % data[0]
                # # print(sql)
                # cursor.execute(sql)
                # newsGeted = cursor.fetchall()
                # # print(newsGeted)
                # dict = {'标题': newsGeted[0][1], '内容': newsGeted[0][2], '时间': str(newsGeted[0][3]), '封面': newsGeted[0][4],
                #         'ID': newsGeted[0][0]}
                # results.append(dict)
                if i != len(datas) - 1:
                    sql += str(data[0]) + ','
                    i += 1
                else:
                    sql += str(data[0]) + ') order by publish_data desc'
            print(sql)
            cursor.execute(sql)
            newsGeted = cursor.fetchall()
            for item in newsGeted:
                dict = {'标题': item[1], '内容': item[2], '时间': str(item[3]),
                        '封面': item[4],
                        'ID': item[0]}
                print(dict)
                results.append(dict)
        # print(results)

    # 关闭数据库连接
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
    sql = "select * from news order by publish_data desc"
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
    news_object = request.form['object']
    sql = "INSERT INTO `news`(`NEWS_TITLE`, `NEWS_CONTENT`, `PUBLISH_DATA`,`COVER`) VALUES (\'%s\',\'%s\',now(),\'%s\')" % (
        news_title, news_path, news_cover)
    print(sql)
    print(news_path)
    cursor.execute(sql)
    db.commit()

    sql = 'select MAX(news_id) from news'
    cursor.execute(sql)
    news_id = cursor.fetchall()

    sql = 'INSERT INTO `news_tag` (`TAG_ID`, `NEWS_ID`, `TAG`) VALUES (NULL, \'%s\', \'%s\')' % (
        news_id[0][0], news_object)
    print(sql)
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


# 查询队名供关注使用
# 方法：GET
# 参数：tablename:表名
# 返回: json
@app.route('/queryteamname')
def queryteamname():
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
            sql = 'select name from team,yingchao_ranking where team.TEAM_ID=yingchao_ranking.TEAM_ID'

        elif table_name == 'yijia':
            sql = 'select name from team,yijia_ranking where team.TEAM_ID=yijia_ranking.TEAM_ID'

        elif table_name == 'xijia':
            sql = 'select name from team,xijia_ranking where team.TEAM_ID=xijia_ranking.TEAM_ID'

        elif table_name == 'dejia':
            sql = 'select name from team,dejia_ranking where team.TEAM_ID=dejia_ranking.TEAM_ID'

        elif table_name == 'zhongchao':
            sql = 'select name from team,zhongchao_ranking where team.TEAM_ID=zhongchao_ranking.TEAM_ID'

        cursor.execute(sql)
        datas = cursor.fetchall()
        results = []
        for data in datas:
            result = {'队名': data[0]}
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


# 预测比赛结果
# 参数：team1 主队
#      team2 客队
# 返回结果
@app.route('/predict', methods=['get'])
def predict():
    team1 = request.values.get('team1')
    team2 = request.values.get('team2')
    team1 += ' '
    team2 += ' '
    print(team1)
    print(team2)
    teamsInput = encoder.transform([[team1, team2]])
    prediction = forest_clf.predict(teamsInput)
    result = {'result': int(prediction[0])}
    return str(prediction[0])


# 关注球队列表
# 参数：username 用户id
# 返回json 球队列表
@app.route('/getfollowed', methods=['get'])
def getFollowed():
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
    sql = 'SELECT team.NAME from team,follow,user WHERE follow.TEAM_ID=team.TEAM_ID and follow.USER_ID=user.USER_ID and user.NAME=\'%s\''%userName
    cursor.execute(sql)
    datas = cursor.fetchall()
    results = []
    for data in datas:
        result = {'队名': data[0]}
        results.append(result)

    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()
    return json.dumps(results, ensure_ascii=False)


if __name__ == '__main__':
    # 上传服务器时要将host改成0.0.0.0
    # 调试的时候host使用127.0.0.1
    app.run(debug=True, port=8000, host='0.0.0.0')
