#!/usr/bin/env python
# -*- coding:utf-8 -*-  
"""
@author: YuFei 
@email: yufei6808@163.com
@site: http://www.antuan.com
@version: 0.0.1
@date: 2019-02-00
@explain: 功能介绍
"""

# -*- coding: utf-8 -*-
import datetime
import json
import logging
import os
import re
import time
import uuid

import sys
reload(sys)
sys.setdefaultencoding('utf8')

import pandas as pd
from contextlib import closing

import elasticsearch
import requests
from elasticsearch.helpers import bulk

from selenium import webdriver

from fake_useragent import UserAgent
from moviepy.editor import VideoFileClip

from env import config
from qiniu import Qiniu
from utils.util import CheloExtendedLogger, count_str, query_cut, del_item_from_list
from utils.tool import obj2int, makedirs
from utils.video_filter import video_filter

requests.packages.urllib3.disable_warnings()


ua = UserAgent()
_qiniu = Qiniu()
base_url = os.path.split(os.path.realpath(__file__))[0]
es_client = elasticsearch.Elasticsearch(config.es_addr, timeout=30)
logging.setLoggerClass(CheloExtendedLogger)
douyin_logger = logging.getLogger("douyin_sync")
CHROME_DRIVER = os.path.join(base_url, 'utils/chromedriver')


class DouYin(object):
    ua = ua.random

    def __init__(self):
        option = webdriver.ChromeOptions()
        option.add_argument('--headless')
        option.add_argument('disable-infobars')
        option.add_argument('start-maximized')
        option.add_argument('--no-sandbox')
        option.add_argument('--no-zygote')
        option.add_argument('--disable-dev-shm-usage')
        option.add_argument('--disable-extensions')

        option.add_argument('--disable-gpu')
        option.add_argument('--user-agent={}'.format(self.ua))
        # option.binary_location=CHROME_DRIVER

        self.driver = webdriver.Chrome(CHROME_DRIVER, chrome_options=option)
        # self.driver = webdriver.PhantomJS()
        self.driver.get('https://www.baidu.com')

    def __get_sig_dytk(self, uid):
        #  获取到 tac 和 dytk
        p1 = r'<script>tac=\'(?P<tac>[\W\w]{150,300}?)\'</script>'
        pattern1 = re.compile(p1)

        p2 = r'dytk ?: ?\'(?P<dytk>[0-9a-z]*?)\''
        pattern2 = re.compile(p2)
        html = requests.get(f'https://www.iesdouyin.com/share/user/{uid}?u_code=1724gaic5&timestamp={int(time.time())}',
                            headers={'user-agent': self.ua}).text
        tac = pattern1.search(html).group('tac')
        dytk = pattern2.search(html).group('dytk')
        # print('22',tac,dytk)

        #  拼接html页面
        s_tac = "tac=\'{}\'".format(tac)
        # print(s_tac)
        s1 = """
        <!DOCTYPE html>
        <html style="font-size: 50px;"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">  <title>douyin_signature</title>
        </head>
        <body></body>
        </html>
        <script type="text/javascript">
        """

        s2 = """
        !function(t) {
            if (t.__M = t.__M || {},
            !t.__M.require) {
                var e, n, r = document.getElementsByTagName("head")[0], i = {}, o = {}, a = {}, u = {}, c = {}, s = {}, l = function(t, n) {
                    if (!(t in u)) {
                        u[t] = !0;
                        var i = document.createElement("script");
                        if (n) {
                            var o = setTimeout(n, e.timeout);
                            i.onerror = function() {
                                clearTimeout(o),
                                n()
                            }
                            ;
                            var a = function() {
                                clearTimeout(o)
                            };
                            "onload"in i ? i.onload = a : i.onreadystatechange = function() {
                                ("loaded" === this.readyState || "complete" === this.readyState) && a()
                            }
                        }
                        return i.type = "text/javascript",
                        i.src = t,
                        r.appendChild(i),
                        i
                    }
                }, f = function(t, e, n) {
                    var r = i[t] || (i[t] = []);
                    r.push(e);
                    var o, a = c[t] || c[t + ".js"] || {}, u = a.pkg;
                    o = u ? s[u].url || s[u].uri : a.url || a.uri || t,
                    l(o, n && function() {
                        n(t)
                    }
                    )
                };
                n = function(t, e) {
                    "function" != typeof e && (e = arguments[2]),
                    t = t.replace(/\.js$/i, ""),
                    o[t] = e;
                    var n = i[t];
                    if (n) {
                        for (var r = 0, a = n.length; a > r; r++)
                            n[r]();
                        delete i[t]
                    }
                }
                ,
                e = function(t) {
                    if (t && t.splice)
                        return e.async.apply(this, arguments);
                    t = e.alias(t);
                    var n = a[t];
                    if (n)
                        return n.exports;
                    var r = o[t];
                    if (!r)
                        throw "[ModJS] Cannot find module `" + t + "`";
                    n = a[t] = {
                        exports: {}
                    };
                    var i = "function" == typeof r ? r.apply(n, [e, n.exports, n]) : r;
                    return i && (n.exports = i),
                    n.exports && !n.exports["default"] && Object.defineProperty && Object.isExtensible(n.exports) && Object.defineProperty(n.exports, "default", {
                        value: n.exports
                    }),
                    n.exports
                }
                ,
                e.async = function(n, r, i) {
                    function a(t) {
                        for (var n, r = 0, h = t.length; h > r; r++) {
                            var p = e.alias(t[r]);
                            p in o ? (n = c[p] || c[p + ".js"],
                            n && "deps"in n && a(n.deps)) : p in s || (s[p] = !0,
                            l++,
                            f(p, u, i),
                            n = c[p] || c[p + ".js"],
                            n && "deps"in n && a(n.deps))
                        }
                    }
                    function u() {
                        if (0 === l--) {
                            for (var i = [], o = 0, a = n.length; a > o; o++)
                                i[o] = e(n[o]);
                            r && r.apply(t, i)
                        }
                    }
                    "string" == typeof n && (n = [n]);
                    var s = {}
                      , l = 0;
                    a(n),
                    u()
                }
                ,
                e.resourceMap = function(t) {
                    var e, n;
                    n = t.res;
                    for (e in n)
                        n.hasOwnProperty(e) && (c[e] = n[e]);
                    n = t.pkg;
                    for (e in n)
                        n.hasOwnProperty(e) && (s[e] = n[e])
                }
                ,
                e.loadJs = function(t) {
                    l(t)
                }
                ,
                e.loadCss = function(t) {
                    if (t.content) {
                        var e = document.createElement("style");
                        e.type = "text/css",
                        e.styleSheet ? e.styleSheet.cssText = t.content : e.innerHTML = t.content,
                        r.appendChild(e)
                    } else if (t.url) {
                        var n = document.createElement("link");
                        n.href = t.url,
                        n.rel = "stylesheet",
                        n.type = "text/css",
                        r.appendChild(n)
                    }
                }
                ,
                e.alias = function(t) {
                    return t.replace(/\.js$/i, "")
                }
                ,
                e.timeout = 5e3,
                t.__M.define = n,
                t.__M.require = e
            }
        }(this)
        __M.define("douyin_falcon:node_modules/byted-acrawler/dist/runtime", function(l, e) {
            Function(function(l) {
                return 'e(e,a,r){(b[e]||(b[e]=t("x,y","x "+e+" y")(r,a)}a(e,a,r){(k[r]||(k[r]=t("x,y","new x[y]("+Array(r+1).join(",x[y]")(1)+")")(e,a)}r(e,a,r){n,t,s={},b=s.d=r?r.d+1:0;for(s["$"+b]=s,t=0;t<b;t)s[n="$"+t]=r[n];for(t=0,b=s=a;t<b;t)s[t]=a[t];c(e,0,s)}c(t,b,k){u(e){v[x]=e}f{g=,ting(bg)}l{try{y=c(t,b,k)}catch(e){h=e,y=l}}for(h,y,d,g,v=[],x=0;;)switch(g=){case 1:u(!)4:f5:u((e){a=0,r=e;{c=a<r;c&&u(e[a]),c}}(6:y=,u((y8:if(g=,lg,g=,y===c)b+=g;else if(y!==l)y9:c10:u(s(11:y=,u(+y)12:for(y=f,d=[],g=0;g<y;g)d[g]=y.charCodeAt(g)^g+y;u(String.fromCharCode.apply(null,d13:y=,h=delete [y]14:59:u((g=)?(y=x,v.slice(x-=g,y:[])61:u([])62:g=,k[0]=65599*k[0]+k[1].charCodeAt(g)>>>065:h=,y=,[y]=h66:u(e(t[b],,67:y=,d=,u((g=).x===c?r(g.y,y,k):g.apply(d,y68:u(e((g=t[b])<"<"?(b--,f):g+g,,70:u(!1)71:n72:+f73:u(parseInt(f,3675:if(){bcase 74:g=<<16>>16g76:u(k[])77:y=,u([y])78:g=,u(a(v,x-=g+1,g79:g=,u(k["$"+g])81:h=,[f]=h82:u([f])83:h=,k[]=h84:!085:void 086:u(v[x-1])88:h=,y=,h,y89:u({e{r(e.y,arguments,k)}e.y=f,e.x=c,e})90:null91:h93:h=0:;default:u((g<<16>>16)-16)}}n=this,t=n.Function,s=Object.keys||(e){a={},r=0;for(c in e)a[r]=c;a=r,a},b={},k={};r'.replace(/[-]/g, function(e) {
                    return l[15 & e.charCodeAt(0)]
                })
            }("v[x++]=v[--x]t.charCodeAt(b++)-32function return ))++.substrvar .length(),b+=;break;case ;break}".split("")))()('gr$Daten Иb/s!l y͒yĹg,(lfi~ah`{mv,-n|jqewVxp{rvmmx,&effkx[!cs"l".Pq%widthl"@q&heightl"vr*getContextx$"2d[!cs#l#,*;?|u.|uc{uq$fontl#vr(fillTextx$$龘ฑภ경2<[#c}l#2q*shadowBlurl#1q-shadowOffsetXl#$$limeq+shadowColorl#vr#arcx88802[%c}l#vr&strokex[ c}l"v,)}eOmyoZB]mx[ cs!0s$l$Pb<k7l l!r&lengthb%^l$1+s$jl  s#i$1ek1s$gr#tack4)zgr#tac$! +0o![#cj?o ]!l$b%s"o ]!l"l$b*b^0d#>>>s!0s%yA0s"l"l!r&lengthb<k+l"^l"1+s"jl  s&l&z0l!$ +["cs\\'(0l#i\\'1ps9wxb&s() &{s)/s(gr&Stringr,fromCharCodes)0s*yWl ._b&s o!])l l Jb<k$.aj;l .Tb<k$.gj/l .^b<k&i"-4j!+& s+yPo!]+s!l!l Hd>&l!l Bd>&+l!l <d>&+l!l 6d>&+l!l &+ s,y=o!o!]/q"13o!l q"10o!],l 2d>& s.{s-yMo!o!]0q"13o!]*Ld<l 4d#>>>b|s!o!l q"10o!],l!& s/yIo!o!].q"13o!],o!]*Jd<l 6d#>>>b|&o!]+l &+ s0l-l!&l-l!i\\'1z141z4b/@d<l"b|&+l-l(l!b^&+l-l&zl\\'g,)gk}ejo{cm,)|yn~Lij~em["cl$b%@d<l&zl\\'l $ +["cl$b%b|&+l-l%8d<@b|l!b^&+ q$sign ', [Object.defineProperty(e, "__esModule", {
                value: !0
            })])
        });
                dycs = __M.require("douyin_falcon:node_modules/byted-acrawler/dist/runtime")
                signc = dycs.sign(&&&)
                document.title=signc
                document.write(signc)
        </script>
        """
        s2 = s2.replace('&&&', uid)
        file = os.path.join(base_url, './' + uuid.uuid4().hex + '.html')
        with open(file, 'w', encoding='utf-8') as fw:
            fw.write(s1 + s_tac + s2)

        self.driver.get('file://' + file)
        sig = self.driver.title
        os.remove(file)
        return sig, dytk

    def fetch_all_video(self, uid):
        # sig, dytk = self.__get_sig_dytk(uid)
        has_more = 1
        max_cursor = 0
        headers = {'user-agent': self.ua}
        result = []
        while has_more == 1:
            try:
                sig, dytk = self.__get_sig_dytk(uid)
            except Exception as e:
                douyin_logger.error(e)
                continue

            r = requests.get(
                'https://www.douyin.com/aweme/v1/aweme/post/?user_id={}&count=21&max_cursor={}&aid=1128&_signature={}&dytk={}'.format(
                    uid, max_cursor, sig, dytk), headers=headers)
            if r.status_code == 200:
                data = json.loads(r.text)
                if data['status_code'] == 0:
                    if data['aweme_list']:
                        has_more = data['has_more']
                        max_cursor = data['max_cursor']
                        headers = r.request.headers
                        result.extend(data['aweme_list'])
                        time.sleep(1)
                        continue
                    else:
                        result.extend(data['aweme_list'])
            break

        return result

    def checkout_user_agent(self):
        self.driver.quit()
        return self.ua
    
    
    def process_user(self):
        """
            获取所有用户的视频地址，并存入文件
            从redis中取max_cursor取最新的视频
            :return:
        """
        all_videos = {}
        douyin_logger.info('start get video url....')
        user_dict = json.loads(config.rds10.get('douyin_user_dict'))

        for user_id, user_list in user_dict.items():
            videos = self.fetch_all_video(user_id)
            all_videos[user_id] = videos
            print(user_id, len(videos))

        douyin_logger.info('end get video url....')

        if all_videos:
            with open('douyin_video.json', 'w') as f:
                f.write(json.dumps(all_videos))

        douyin_logger.info('save douyin video successful....')


def download(url, full_name):
    """
    每个视频会有多个地址，取key下载的地址下载到本地存档
    :param url: 原始地址
    :param full_name: 本地路径
    :return:
    """
    headers_new = {
        'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
        'accept-encoding': 'gzip, deflate, br',
        'accept-language': 'zh-CN,zh;q=0.9',
        'cache-control': 'max-age=0',
        'upgrade-insecure-requests': '1',
        'user-agent': 'Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; MI 4S Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.146 Mobile Safari/537.36 XiaoMi/MiuiBrowser/9.1.3',
    }
    size = 0
    is_success = False
    if os.path.exists(full_name):
        douyin_logger.info(f'have dowmloaded {full_name}')
        return True

    folder, base_name = os.path.abspath(full_name).rsplit('/', 1)
    makedirs(folder)
    with closing(requests.get(url, headers=headers_new, stream=True, verify=False)) as response:
        chunk_size = 1024
        print(response.status_code)
        if response.status_code == 200:
            content_size = int(response.headers['content-length'])
            if content_size <= 121455:  # 视频不存在，3秒
                return False

            douyin_logger.info('[%s||%s||文件大小]:%0.2f MB\n' % (url, full_name, content_size / chunk_size / 1024))
            with open(full_name, "wb") as file:
                for data in response.iter_content(chunk_size=chunk_size):
                    file.write(data)
                    size += len(data)
                    file.flush()
                douyin_logger.info('[下载进度]:%.2f%%' % float(size / content_size * 100))
            is_success = True

    return is_success


def process_download():
    """
    读取所有用户的视频地址并下载
    批量保存到es，设置已下载缓存，本次执行写入文件，供给运营
    :return:
    """
    with open('douyin_video.json', 'r') as f:
        all_videos = json.loads(f.read())

    es_content = []
    actions = []
    douyin_logger.info('start dowload video....')
    user_dict = json.loads(config.rds10.get('douyin_user_dict'))
    # for k, v in all_videos.items():
    for k, note_v in user_dict.items():
        v = all_videos.get(k, [])
        douyin_logger.info(f'download for user {k}.')
        es_exists, id_flag, _ = get_es_video(note_v[1])

        need_crawl = len(v)
        for video_item in v:
            video_id, title, filename, create_time = get_public(video_item, k)
            if not title:
                continue

            if video_id in es_exists:
                need_crawl -= 1
                douyin_logger.info(f'{video_id} exists! continue....')
                continue

            is_success = process_download_video(actions, es_content, k, video_item)
            if is_success:
                need_crawl -= 1

        if need_crawl == 0:
            douyin_logger.info(f'{k} crawl {len(v)} complete!')
        else:
            douyin_logger.error(f'{k} crawl failed! total {len(v)}, remain {need_crawl}')

        if actions:
            douyin_logger.info('bulk insert to es..')
            bulk(es_client, actions=actions)
            douyin_logger.info(f'bulk insert to es successful, length:{len(actions)}')
            actions = []

    if es_content:
        douyin_logger.info('save to file..')
        now = datetime.datetime.now().strftime('%Y%m%d%H%M%S')
        to_csv = pd.DataFrame(es_content,
                              columns=['用户id', '用户名', '视频id', '标题', '视频地址', '视频本地地址',
                                       '七牛地址', '创建时间', '转发数', '点赞数', '过滤'])
        file_path = f'{base_url}/dy_video/douyin_{now}.csv'
        to_csv.to_csv(file_path)
        douyin_logger.info(f'save to file successful, path:{file_path}')


def get_es_video(user_id):
    res = es_client.search(index='distribution', doc_type='note_new', body={
        "query": {
            "bool": {
                "must": [
                    {"term": {"source": {"value": "douyin"}}},
                    {"term": {"user_id": {"value": user_id}}}
                ]
            }},
        "_source": ["id","flag", "title", 'source_url'],
        "size": 9999
    })['hits']['hits']

    id_flag = {item['_source']['id']: item.get('flag', None) for item in res}
    return list(id_flag.keys()), id_flag, res


def process_download_video(actions, es_content, user_id, video_item):
    """
    下载单个视频，转存七牛，构建es结构数据
    :param actions:
    :param es_content:
    :param user_id:
    :param video_item:
    :return:
    """
    video_id, title, filename, create_time = get_public(video_item, user_id)

    full_name = f'{base_url}/{filename}'
    douyin_logger.info(f'id:{video_id},\n title: {title} \n local_path:{full_name}')

    is_success = True
    play_addr= None
    if not os.path.exists(full_name):
        for play_addr in video_item['video']['play_addr']['url_list']:
            try:
                is_success = download(play_addr, full_name)
                play_addr= play_addr
                break
            except Exception as e:
                is_success = False
                douyin_logger.error(e)

    if is_success:
        flag = set_flag(title, full_name)
        douyin_logger.info(f'video origin_url: {play_addr}')

        cover_url, video_url = push_to_qiniu(filename, full_name, video_item)
        if not video_url:
            return
        # cover_url, video_url = video_item['video']['cover']['url_list'][0] if len(
        #     video_item['video']['cover']['url_list']) else '', play_addr
        es_item = build_es_action(cover_url, user_id, play_addr, video_item, video_url)
        es_item['flag'] = flag
        actions.append({
            '_op_type': 'index',
            '_index': 'distribution',
            '_type': 'note_new',
            '_id': es_item['id'],
            '_source': es_item
        })
        es_content.append(
            [user_id, user_dict[user_id][0], video_id, title, play_addr, full_name, video_url, create_time,
             video_item['statistics']['forward_count'], video_item['statistics']['digg_count'], flag])

    return is_success


def build_es_action(cover_url, user_id, play_addr, video_item, video_url):
    video_id, title, filename, create_time = get_public(video_item, user_id)
    full_name = f'{base_url}/{filename}'
    # 横屏3， 竖屏4
    direction = 3
    if obj2int(video_item['video']['width']) < obj2int(video_item['video']['height']):
        direction = 4

    rel_item = {
        'create_time': create_time,
        'user_name': user_dict[user_id][0],
        'user_id': user_dict[user_id][1],
        'title': '{}...'.format(title[:30]) if len(title) > 30 else title,
        'source_url': play_addr,
        'content': json.dumps([{'type': 1, 'text': title}], ensure_ascii=False),
        'community_new': '家人闲聊',
        'community_new_id': 6,
        'source': 'douyin',
        'id': video_id,
        'video': json.dumps(
            {
                'width': video_item['video']['width'],
                'height': video_item['video']['height'],
                'video_play_time': VideoFileClip(filename).duration,
                'video_size': int(os.path.getsize(full_name) / 1024),
                'video_preview_url': cover_url,
                'video_url': video_url,
                'direction': direction
            }
        ),
        'douyin_user_id':user_id
    }
    if not rel_item['title']:
        del rel_item['title']
    return rel_item


def push_to_qiniu(filename, full_name, video_item):
    success, video_url_info = _qiniu.up_io(open(full_name, 'rb'), key=filename)
    video_url = None
    if success:
        video_url = video_url_info['url']
        douyin_logger.info(f'video qiniu_url: {video_url}')
    else:
        douyin_logger.error(f'transfer to qiniu failed: {filename}')

    success, origin_cover_info = _qiniu.up_url(video_item['video']['cover']['url_list'][0])
    if success:
        cover_url = origin_cover_info['url']
        douyin_logger.info(f'video cover_url: {cover_url}')
    else:
        cover_url = video_item['video']['cover']['url_list'][0]
        douyin_logger.error(f'transfer to qiniu failed: {filename}')
    return cover_url, video_url


def get_public(video_item, user_id):
    """
    公共参数
    :param video_item:
    :param user_id:
    :return:
    """
    video_id = video_item['video']['play_addr']['uri']
    if 'desc' in video_item:
        title = video_item['desc']
    elif 'share_desc' in video_item['share_info']:
        title = video_item['share_info']['share_desc']
    else:
        return None,None,None,None
    filename = f'dy_video/{str(user_id)}/{video_id}.mp4'
    # full_name = f'{base_url}/{filename}'
    if 'create_time' in 'video_item':
        create_time = datetime.datetime.fromtimestamp(video_item['create_time']).strftime('%Y-%m-%d %H:%M:%S')
    else:
        create_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    return video_id, title.replace('@抖音小助手', ''), filename, create_time

def set_flag(title, full_name):
    if not title or count_str(title) > 6:
        flag = -1
    elif video_filter(full_name) == '轮播':
        flag = -2
    else:
        try:
            category_ones = [it['category_one'] for it in query_cut(title) if it.get('category_one')]
            if category_ones and del_item_from_list(category_ones, ['人群', '职业']):
                flag = 1
            else:
                os.system(
                    f'/usr/local/anaconda3/envs/py36_tf_env/bin/python /home/hadoop/svn/comm_py2/tf_pack/classification-cnn-rnn_douyin/predict_cnn.py "{title}"')
                with open('/usr/local/api/py3_project/RobotScript/weibo/cnn.txt', 'r') as fh:
                    res = fh.read()
                if '不相关' in res:
                    flag = -3
                else:
                    flag = 1
        except:
            flag = 1

    return flag

def update_flag():
    user_dict = json.loads(config.rds10.get('douyin_user_dict'))
    for k, note_v in user_dict.items():
        douyin_logger.info(f'update user: {note_v[1]} \'s flag')
        actions = []
        es_exists, id_flag, res = get_es_video(note_v[1])
        for es_record in res:
            title = es_record['_source'].get('title')
            if not title:
                continue

            rid = es_record['_source']['id']
            full_name = f'{base_url}/dy_video/{k}/{rid}.mp4'
            if not os.path.exists(full_name):
                download(es_record['_source']['source_url'], full_name)
            flag = set_flag(title, full_name)
            actions.append({
                 '_op_type': 'update', '_index': 'distribution', '_type': 'note_new', '_id': rid, 'doc': {'flag': flag}
            })
        if actions:
            bulk(es_client, actions)


def init_users():
    user_init = {
        '98358605641': ['装修报', 0000000]
    }

    # if not config.rds10.exists('douyin_user_dict'):
    # for k, v in user_init.items():
    #     config.rds10.delete(f'douyin_max_cursor_{k}')

    # if not config.rds10.exists('douyin_user_dict'):
    config.rds10.set('douyin_user_dict', json.dumps(user_init))

    global user_dict
    user_dict = json.loads(config.rds10.get('douyin_user_dict'))


if __name__ == '__main__':
    douyin_logger.info(f'start douyin spider....{datetime.datetime.now()}')
    try:
        init_users()
        dy = DouYin()
        dy.process_user()
        process_download()
        # update_flag()
    except Exception as e:
        douyin_logger.error(e)

    douyin_logger.info(f'end douyin spider....{datetime.datetime.now()}')