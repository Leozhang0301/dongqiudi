from bs4 import BeautifulSoup
import requests

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                  'Chrome/87.0.4280.88 Safari/537.36 '
}
url = "https://www.dongqiudi.com/data/231"
html = requests.get(url=url, headers=headers)

soup = BeautifulSoup(html.text, 'lxml')
final = soup.find(class_='group-con')
datas = soup.find_all(class_='group-td')
for data in datas:
    print(data)


