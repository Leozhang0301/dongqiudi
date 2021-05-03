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
soup = soup.find(class_='game-live')
# matches by day
matches = soup.find_all(class_='match-list')

match_dic = {'date': 0, 'time': 0, 'league': 0, 'home_team': 0, 'away_team': 0, 'result': 0}
print(type(matches))
for match in matches:
    print(type(match))
    print(match)
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
            if item.find(class_='round').text == '中超':
                # 插入数据
                sql = "INSERT INTO `matches`(`DATE`, `TIME`, `HOME_TEAM`, `AWAY_TEAM`, `RESULT`, `LEAGUE`) VALUES (\'%s\'," \
                      "\'%s\',\'%s\',\'%s\',\'%s\',\'%s\') " % (
                          match_dic['date'], match_dic['time'], hash_map[match_dic['home_team']], hash_map[match_dic['away_team']],
                          match_dic['result'],
                          match_dic['league'],)
            else:
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
