import sys
import nltk
from urllib import urlopen

url_sys = sys.argv[1]
url="https://www.federalreserve.gov/newsevents/pressreleases/monetary"+url_sys+"a.htm"
html = urlopen(url).read()
raw = nltk.clean_html(html)
#print(raw)
file2write = open("clean_this_file.txt",'w')
file2write.write(raw)
file2write.close()
