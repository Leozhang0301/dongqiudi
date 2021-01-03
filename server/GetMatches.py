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
leagueList = {'英超', '德甲', '意甲', '中超', '西甲'}

url = "https://www.dongqiudi.com/"

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                  'Chrome/87.0.4280.88 Safari/537.36 '
}

# 清空表单
sql = "truncate table matches"
cursor.execute(sql)

# 网络请求得到爬虫
html = requests.get(url=url, headers=headers)

soup = BeautifulSoup(html.text, 'lxml')
# matches by day
matches = soup.find_all(class_='match-list')

match_dic = {'date': 0, 'time': 0, 'league': 0, 'home_team': 0, 'away_team': 0, 'result': 0}
for match in matches:
    # 每天的match
    match_dic['date'] = match.find(class_='date').text
    items = match.find_all(class_='match-item')
    for item in items:
        # 每场比赛
        if item.find(class_='round').text in leagueList:
            # 比赛属于五大联赛的
            match_dic['time'] = item.find(class_='start-min').text
            match_dic['league'] = item.find(class_='round').text
            match_dic['home_team'] = item.find(class_='teama-name').text
            match_dic['away_team'] = item.find(class_='teamb-name').text
            print(item.find(class_='feature'))
            if item.find(class_='feature') is None:
                match_dic['result']=item.find(class_='score').text
            else:
                match_dic['result'] = item.find(class_='feature').text
            print(match_dic)
            # 插入数据
            sql = "INSERT INTO `matches`(`DATE`, `TIME`, `HOME_TEAM`, `AWAY_TEAM`, `RESULT`, `LEAGUE`) VALUES (\'%s\'," \
                  "\'%s\',\'%s\',\'%s\',\'%s\',\'%s\') " % (
                      match_dic['date'], match_dic['time'], match_dic['home_team'], match_dic['away_team'],
                      match_dic['result'],
                      match_dic['league'],)
            print(sql)
            cursor.execute(sql)
db.commit()
print('matches insert successful')

cursor.close()
db.close()
server.close()
