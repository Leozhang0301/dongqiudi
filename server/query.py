import json, flask
from flask import request
import pymysql
from sshtunnel import SSHTunnelForwarder

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


@app.route('/queryrank')
def login():
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

    app = flask.Flask(__name__)

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
        return json.dumps(results, ensure_ascii=False)


    else:
        resu = {'code': 1001, 'message': '参数不能为空'}
        return json.dumps(resu, ensure_ascii=False)

    # 关闭数据库连接
    cursor.close()
    db.close()
    server.close()


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
