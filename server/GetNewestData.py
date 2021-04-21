from sshtunnel import SSHTunnelForwarder
import pymysql
from bs4 import BeautifulSoup
import requests

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
# sql = "select TEAM_ID from team where NAME='利物浦'"
# cursor.execute(sql)
# data = cursor.fetchall()
# print(data[0][0])

# 中超改名hash表
hash_map = {'广州': '广州恒大淘宝',
            '北京国安': '北京中赫国安',
            '上海海港': '上海上港',
            '山东泰山': '山东鲁能泰山',
            '重庆两江竞技': '重庆当代',
            '上海申花': '上海绿地申花',
            '长春亚泰': '江苏苏宁',
            '河南嵩山龙门': '河南建业',
            '青岛': '青岛黄海青港',
            '河北': '河北华夏幸福',
            '沧州雄狮': '石家庄永昌',
            '武汉': '武汉卓尔',
            '大连人': '大连人',
            '深圳': '深圳佳兆业',
            '天津津门虎': '天津泰达',
            '广州城': '广州富力'}

# url和表明对应  分别对应英超，意甲，西甲，德甲
url = ("https://www.dongqiudi.com/data/1",
       "https://www.dongqiudi.com/data/2",
       "https://www.dongqiudi.com/data/3",
       "https://www.dongqiudi.com/data/4",
       "https://www.dongqiudi.com/data/231")
table_name = ("yingchao_ranking",
              "yijia_ranking",
              "xijia_ranking",
              "dejia_ranking",
              "zhongchao_ranking")
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                  'Chrome/87.0.4280.88 Safari/537.36 '
}

# 循环4遍 分别得到四大联赛的积分数据插入到数据库
for num in range(5):
    # 清空表单
    sql = "truncate table " + table_name[num]
    cursor.execute(sql)

    # 网络请求得到爬虫
    html = requests.get(url=url[num], headers=headers)

    soup = BeautifulSoup(html.text, 'lxml')
    datas = soup.find_all(class_='td')
    for data in datas:
        items = data.find_all('span')
        i = 0
        dic = {'duiming': 0, 'changci': 0, 'sheng': 0, 'ping': 0, 'fu': 0, 'jin': 0, 'shi': 0, 'jingsheng': 0,
               'jifen': 0}
        for item in items:
            i += 1
            if i != 1 and i != 2:
                if i == 3:
                    dic['changci'] = item.text
                elif i == 4:
                    dic['sheng'] = item.text
                elif i == 5:
                    dic['ping'] = item.text
                elif i == 6:
                    dic['fu'] = item.text
                elif i == 7:
                    dic['jin'] = item.text
                elif i == 8:
                    dic['shi'] = item.text
                elif i == 9:
                    dic['jingsheng'] = item.text
                elif i == 10:
                    dic['jifen'] = item.text
            # 插入球队表
            # elif i == 2:
            #     print(item.find('img')['src'], item.text)
            #     # 下图片
            #     IMG_URL = item.find('img')['src']
            #     r = requests.get(IMG_URL, stream=True)
            #     path = 'C:/Users/Administrator/Desktop/pic/' + item.text + '.png'
            #     with open(path, 'wb') as f:
            #         for chunk in r.iter_content(chunk_size=32):
            #             f.write(chunk)
            #     sql = 'insert into team (NAME,TEAM_PIC) values(\'%s\',\'%s\')' % (item.text, item.text + '.png')
            #     # sql = 'insert into team (NAME,TEAM_PIC) values(1,1)'
            #     cursor.execute(sql)
            #     db.commit()
        # print(dic)

        name = data.span.next_sibling.text
        # 如果是中超队名需要转换hash表
        if num == 4:
            name = hash_map[name]
        sql = "select TEAM_ID from team where NAME=\'%s\'" % name
        cursor.execute(sql)
        data = cursor.fetchall()
        dic['duiming'] = data[0][0]
        print(dic)
        sql = 'insert into %s (TEAM_ID,CHANGCI,SHENG,PING,FU,JIN,SHI,JINGSHENG,JIFEN)' \
              'values(\'%s\',\'%s\',\'%s\',\'%s\',\'%s\',\'%s\',\'%s\',\'%s\',\'%s\')' % (
                  table_name[num], dic['duiming'],
                  dic['changci'],
                  dic['sheng'],
                  dic['ping'],
                  dic['fu'],
                  dic['jin'],
                  dic['shi'],
                  dic['jingsheng'],
                  dic['jifen'])

        cursor.execute(sql)
        db.commit()
    print(table_name[num], "insert successful")

# sql = "select * from test"
#
# cursor.execute(sql)
#
# data = cursor.fetchall()
# for item in data:
#     print(item)
#     print(item[0])
#
# sql = "truncate table test"
# cursor.execute(sql)
#
# sql = "select * from test"
# cursor.execute(sql)
# data = cursor.fetchall()
# print(data)
#
cursor.close()
db.close()
server.close()
