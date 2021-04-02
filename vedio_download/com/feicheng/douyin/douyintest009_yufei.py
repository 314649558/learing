#!/usr/bin/env python
# -*- coding:utf-8 -*-  
"""
@author: YuFei 
@email: yufei6808@163.com
@site: http://www.antuan.com
@version: 0.0.1
@date: 2019-01-25
@explain: 抖音模拟手机下载
@pip3 install requests
"""

import requests
import random
import time,sys,os,re,ssl,json
from ipaddress import ip_address
from contextlib import closing


def get_heads():
    rip = ip_address('0.0.0.0')
    hds=[{'User-Agent':'Mozilla/5.0 (iPhone; CPU iPhone OS 12_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16B92/iPhone 8 Plus'},\
        {'User-Agent':'Mozilla/5.0 (iPhone; CPU iPhone OS 11_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15F72/iPhone 6s'},\
        {'User-Agent':'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15F79/iPhone 6'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 5.0.0; zh-cn; RNE-AL00 Build/HUAWEIRNE-AL00) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 8.0.0; zh-cn; RNE-AL00 Build/HUAWEIRNE-AL00) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 6.0; zh-cn; HUAWEI MLA-AL10 Build/HUAWEIMLA-AL10) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 7.1.1; zh-cn; OPPO R11 Build/NMF26X) Apple WebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; vivo Y85A Build/OPM1.171019.011) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 5.3.0; zh-cn; RNE-AL00 Build/HUAWEIRNE-AL00) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; PBAM00 Build/OPM1.171019.026) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 9; zh-cn; MI 8 Build/PKQ1.180729.001) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'},\
        {'User-Agent':'Mozilla/5.0 (Linux; U; Android 5.3.1; zh-cn; MI 4S Build/LMY47V) AppleWebKit/537.365 (KHTML, like Gecko) Version/4.0 Chrome/54.0.2785.146 Mobile Safari/537.36 XiaoMi/MiuiBrowser/9.1.3'},]
    while rip.is_private:
        rip = ip_address('.'.join(map(str, (random.randint(0, 255) for _ in range(4)))))
    headers = {
        'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
        'accept-encoding': 'gzip, deflate, br',
        'accept-language': 'zh-CN,zh;q=0.9',
        'pragma': 'no-cache',
        'cache-control': 'no-cache',
        'upgrade-insecure-requests': '1',
        'user-agent': 'Mozilla/5.0 (Linux; U; Android 5.3.1; zh-cn; MI 4S Build/LMY47V) AppleWebKit/537.365 (KHTML, like Gecko) Version/4.0 Chrome/54.0.2785.146 Mobile Safari/537.36 XiaoMi/MiuiBrowser/9.1.3',
        'X-Real-IP': str(rip),
        'X-Forwarded-For': str(rip),
    }
    headers['user-agent'] = hds[random.randint(0,len(hds)-1)]['User-Agent']
    return headers

def get_params():
    params = {'max_cursor':0 ,
                  'user_id':{},
                  'count' : 20,
                  'retry_type' : 'no_retry',
                  'mcc_mnc' : 46001,
                  'iid' : 59352888783,
                  'device_id' : 62662175733,
                  'ac' : 'wifi',
                  'channel' : 'ame_nubiamm',
                  'aid' : 1128,
                  'app_name' : 'aweme',
                  'version_code' : 430,
                  'version_name' : '4.3.0',
                  'device_platform' : 'android',
                  'ssmix' : 'a',
                  'device_type' : 'NX529J',
                  'device_brand' : 'nubia',
                  'language' : 'zh',
                  'os_api' : 22,
                  'os_version' : '5.1.1',
                  'uuid' : 869464022077588,
                  'openudid' : '9a1fcf4674e3f511',
                  'manifest_version_code' : 430,
                  'resolution' : '1080*1920',
                  'dpi' : 480,
                  'update_version_code' : 4302,
                  '_rticket' : 1548401767449,
                  'ts' : 1548401768,
                  'js_sdk_version' : '1.6.4',
                  'as' : 'a1b53b0438266cdc7a0744',
                  'cp' : 'b16ecf5e8ca749cde1Us%5Da',
                  'mas' : '010a32e833e9083ab591bfcb7d7c6f691a2c2cec0c86ecc626c66c'
            }
    return params

def getcount(uid):
    '''
    根据uid获取用户详情
    '''
    userinfo = {}
    baseurl = 'http://aweme-eagle.snssdk.com/aweme/v1/user/?user_id={}&retry_type=no_retry&mcc_mnc=46001&iid=59352888783&device_id=62662175733&ac=wifi&channel=ame_nubiamm&aid=1128&app_name=aweme&version_code=430&version_name=4.3.0&device_platform=android&ssmix=a&device_type=NX529J&device_brand=nubia&language=zh&os_api=22&os_version=5.1.1&uuid=869464022077588&openudid=9a1fcf4674e3f511&manifest_version_code=430&resolution=1080*1920&dpi=480&update_version_code=4302&_rticket=1548401767095&ts=1548401767&js_sdk_version=1.6.4&as=a1f50b4457a61c3c0a8644&cp=ba6fc2507bae43c1e1Sm%5Bq&mas=018cb51d0241ae6c7adc5385fc7f40c19d2c2c6c1c860cc6ccc68c'.format(uid)
    req = requests.get(baseurl).json()
    #上传作品数
    print(req)
    print('上传作品数:',req['user']['aweme_count'])
    aweme_count = req['user']['aweme_count']
    userinfo['aweme_count'] = aweme_count 
    #动态数
    print('动态数:',req['user']['dongtai_count'])
    dongtai_count = req['user']['dongtai_count']
    userinfo['dongtai_count'] = dongtai_count 
    #喜欢数
    print('喜欢作品数:',req['user']['favoriting_count'])
    #city
    city = req['user']['city']
    province = req['user']['province']
    userinfo['city'] = city
    userinfo['province'] = province 
    #头像
    avatar_larger = req['user']['avatar_larger']['url_list'][0]
    userinfo['avatar_larger'] = avatar_larger
    #nickname
    nickname = req['user']['nickname']
    userinfo['nickname'] = nickname
    #签名
    signature = req['user']['signature']
    userinfo['signature'] = signature
    #birthday
    birthday = req['user']['birthday']
    userinfo['birthday'] = birthday
    #关注
    following_count = req['user']['following_count']
    userinfo['following_count'] = following_count
    #粉丝
    mplatform_followers_count = req['user']['mplatform_followers_count']
    userinfo['mplatform_followers_count'] = mplatform_followers_count
    #赞
    total_favorited = req['user']['total_favorited']
    userinfo['total_favorited'] = total_favorited
    #手机
    bind_phone = total_favorited = req['user']['bind_phone']
    userinfo['bind_phone'] = bind_phone
    
    return userinfo

def get_video_url_list(uid):
    '''
    获取视频地址
    Parameters:
        max_cursor: pos起点为0后续请求会告诉下次锚点位置
        has_more: 告诉是否还有下次请求
    Returns:
        aweme_id集合 和 aweme_id：video_urls
    
    '''
    has_more = 1
    max_cursor = 0
    video_names = []
    video_urls = {}
    
    user_url_prefix = 'http://aweme.snssdk.com/aweme/v1/aweme/post/'
    aweme_list = []
    
    while has_more != 0:
        baseurl = 'http://aweme.snssdk.com/aweme/v1/aweme/post/?max_cursor={}&user_id={}&count=20&retry_type=no_retry&mcc_mnc=46001&iid=59352888783&device_id=62662175733&ac=wifi&channel=ame_nubiamm&aid=1128&app_name=aweme&version_code=430&version_name=4.3.0&device_platform=android&ssmix=a&device_type=NX529J&device_brand=nubia&language=zh&os_api=22&os_version=5.1.1&uuid=869464022077588&openudid=9a1fcf4674e3f511&manifest_version_code=430&resolution=1080*1920&dpi=480&update_version_code=4302&_rticket=1548401767449&ts=1548401768&js_sdk_version=1.6.4&as=a1b53b0438266cdc7a0744&cp=b16ecf5e8ca749cde1Us%5Da&mas=010a32e833e9083ab591bfcb7d7c6f691a2c2cec0c86ecc626c66c'.format(max_cursor,uid)
        print('解析视频链接中')

        headers = get_heads()
        req = requests.get(baseurl,headers = headers)
        
        while req.status_code != 200:
            print('ERROR not 200')
            req = requests.get(baseurl,headers = headers)
            
        req = req.json()
        #有时候返回空，要多请求几次{"status_code": 0, "has_more": 0, "max_cursor": 0, "min_cursor": 0, "aweme_list": []}
        try:
            aweme_list = req['aweme_list']
        except:
            aweme_list = []
        while aweme_list == [] or aweme_list == None:
            print('aweme_list []')
            time.sleep(0.2)
            req = requests.get(baseurl,headers = headers,verify=False)
            req = req.json()
            try:
                aweme_list = req['aweme_list']
            except:
                aweme_list = []
        
        try:
            
            aweme_list = req['aweme_list']
            #print('aweme_list',aweme_list)
            #下一个max_cursor的起始点
            max_cursor = req['max_cursor']
            #结束符 0为结束
            has_more = req['has_more']
        except:
            has_more = 1
            max_cursor = 0
            break
        
#     #方法二
#     while aweme_list == []:
#         headers = get_heads()
#         print(headers)
#         req = requests.get(baseurl,headers = headers)
#         print('解析视频链接中')
#         print('status',req.content)
#         req = req.json()
#         print('request:',req)
#         print('aweme_list:',req['aweme_list'])
#         aweme_list += req['aweme_list']
#         print('len',len(aweme_list))
#         #下一个max_cursor的起始点
#         max_cursor = req['max_cursor']
#         #结束符 0为结束
#         has_more = req['has_more']
#     
#     has_more = 1
#     max_cursor = 0
#     video_names = []
#     video_urls = []
#     while has_more != 0:
#         params = get_params()
# 
#         params['user_id'] = uid
#         params['max_cursor'] = max_cursor
#         headers = get_heads()
#         print('start')
#         req = requests.get(user_url_prefix, params=params, headers=headers)
#         print(req)
#  
#         while req.status_code != 200:
#             print('not 200')
#             req = requests.get(user_url_prefix, params=params, headers=headers)
#             html = json.loads(req.text)
#             while html['status_code'] != 0:
#                 print('html not 0')
#                 req = requests.get(user_url_prefix, params=params, headers=headers)
#                 while req.status_code != 200:
#                     print('222 not 200')
#                     req = requests.get(user_url_prefix, params=params, headers=headers)
#                 html = json.loads(req.text)
#              
#             print('modd')   
#             for each in html['aweme_list']:
#                 try:
#                     video_url = each['video']['bit_rate'][0]['play_addr']['url_list'][2]
#                     share_desc = each['desc']
#                 except:
#                     continue
#                 if os.name == 'nt':
#                     for c in r'\/:*?"<>|':
#                         share_desc = share_desc.replace(c, '').strip()
#                 share_id = random.randint(1,1000)
#                 if share_desc in ['抖音-原创音乐短视频社区', 'TikTok', '']:
#                     video_names.append(share_id + '.mp4')
#                 else:
#                     video_names.append(share_id + '-' + share_desc + '.mp4')
#                 #share_urls.append(each['share_info']['share_url'])
#                 video_urls.append(video_url)
#                 
#             max_cursor = html['max_cursor']
#             has_more = html['has_more']
#             print('has_more:',has_more)
#             print('max_cursor:',max_cursor)
#     print(len(video_urls))       
#     print('end')           
#     
#     print('end',aweme_list)
#     print(len(aweme_list))
#     print(aweme_list[0])
#     
#     
        
        for urlinfo in aweme_list:
            try:           
                desc = urlinfo['desc']
                #视频封面
                wideo_img = urlinfo['video']['origin_cover']['url_list'][0]
                #视频无水印地址 
                video_url = urlinfo['video']['bit_rate'][0]['play_addr']['url_list'][0]
                if not video_url:
                    video_playurl_list = urlinfo['video']['play_addr']['url_list']
                    if video_playurl_list == []:
                        video_playurl_list = urlinfo['video']['play_addr_lowbr']['url_list']
                    video_url = video_playurl_list[0]
                
                #作品id
                aweme_id = urlinfo['aweme_id']
                #作品背景音乐地址
                mp3url = urlinfo['music']['play_url']['uri']
            except:
                continue
            
            #视频信息组合
            video_names.append(aweme_id)
            video_info = {}
            video_info['desc'] = desc
            video_info['wideo_img'] = wideo_img
            video_info['video_url'] = video_url
            video_info['mp3url'] = mp3url
            video_urls[aweme_id] = video_info
            
    return video_urls,video_names 
 
 

def video_downloader(video_url, video_name):
    """
    视频下载
    Parameters:
        video_url: 视频地址
        video_name: 视频名和路径
    Returns:
        无
    """
    size = 0
    video_url = video_url
    with closing(requests.get(video_url, headers=get_heads(), stream=True, verify=False)) as response:
        chunk_size = 1024
        content_size = int(response.headers['content-length'])
        if response.status_code == 200:
            sys.stdout.write('  [文件大小]:%0.2f MB\n' % (content_size / chunk_size / 1024))

            with open(video_name, 'wb') as file:
                for data in response.iter_content(chunk_size = chunk_size):
                    file.write(data)
                    size += len(data)
                    file.flush()

                    sys.stdout.write('  [下载进度]:%.2f%%' % float(size / content_size * 100) + '\r')
                    sys.stdout.flush()


def putBeanstack(params):
    repdata = {"ip" : "192.168.1.13", "useragent" : "yufei_douyin_download", "ctype" : 0, "uid" : 0, "type" : 0, "devid" : 0}
    repdata["params"] = params
    repdata = json.dumps(repdata)


    import beanstalkc
    beanstalk = beanstalkc.Connection(host='192.168.1.11', port=13000)
    beanstalk.use('cutter_queue_server')
    beanstalk.put(repdata)
    #job = beanstalk.reserve()
    #print job.body

    return "ok"

def todb(baseurl,userinfo,video_urls,video_names):
    '''
    入库
    '''
    #新头像
    user_id = userinfo['uid']
    print('uid',user_id)
    userinfo['avatar_larger'] = baseurl + user_id + '/icon.jpg'
    
    #异步插库add_user
    params = {}
    data = {}
    data['userid'] = user_id
    data['userinfo'] = userinfo
    params["cmd"] = 'dy.add_user'
    params["data"] = data
    print('user params',params)
    putBeanstack(params)
    
    #新视频封面和新视频url
    for aweme_id in video_names:
        video_info = video_urls[aweme_id]
        video_info['wideo_img'] = baseurl + user_id +'/'+ aweme_id + '.jpg'
        video_info['video_url'] = baseurl + user_id +'/'+ aweme_id + '.mp4'
        
        #异步插库video
        params = {}
        data = {}
        data['userid'] = user_id
        data['aweme_id'] = aweme_id
        data['video_info'] = video_info
        params["cmd"] = 'dy.add_video'
        params["data"] = data
        print('video params',params)
        putBeanstack(params)
    
    print('insert ok',userinfo,video_urls)

def run(user_id,save_dir):    
    user_id = user_id
    save_dir = save_dir
    if not os.path.exists(save_dir):
        os.makedirs(save_dir)
    #获取用户信息
    userinfo = getcount(user_id) 
    userinfo['uid'] = user_id
    #保存路径
    print('userinfo',userinfo)
    #nickname = userinfo['nickname']
    nickname = user_id
    nickname_dir = os.path.join(save_dir, nickname)
    if nickname not in os.listdir(save_dir):
        os.mkdir(nickname_dir)
 
    video_urls,video_names = get_video_url_list(user_id)
    # video_urls = ['https://aweme.snssdk.com/aweme/v1/play/?video_id=v0200fbd0000bbvv5og2sajcg5k5bi0g&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0','http://aweme.snssdk.com/aweme/v1/play/?video_id=v0200ff70000bbvb1fak781i9vlmj9tg&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0','http://aweme.snssdk.com/aweme/v1/play/?video_id=v0200fe70000bbv474hpjc2vij1uve50&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0','http://aweme.snssdk.com/aweme/v1/play/?video_id=v0200fbc0000bbukf14r08m62r7cstlg&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0']
    # video_names = ['asdfa.mp4','asdfafff.mp4','dfsdsdf.mp4','fgdfg.mp4']
    print('end',len(video_urls))
 
    #下载用户头像
    video_url = userinfo['avatar_larger']
    video_name = 'icon.jpg'
    video_path = os.path.join(nickname_dir, video_name)
    video_downloader(video_url,video_path)
    
    #下载视频
    for aweme_id in video_names:
        video_info = video_urls[aweme_id]
        video_url = video_info['video_url']
        #保存视频名
        #name = video_url[desc]
        #video_name = aweme_id + re.sub(r'[\/\\:*?"<>|\ ]', '', name)
        video_name = aweme_id + '.mp4'
        
        video_path = os.path.join(nickname_dir, video_name)
        video_downloader(video_url,video_path)
        
        #保存视频封面
        video_url = video_info['wideo_img']
        video_name = aweme_id + '.jpg'
        video_path = os.path.join(nickname_dir, video_name)
        video_downloader(video_url,video_path)
     
    
    baseurl = 'https://cdn.file0.antuan.com/2018/dy/'
    todb(baseurl,userinfo,video_urls,video_names)

    return len(video_names)
      
def hello():
    """
    打印欢迎界面
    Parameters:
        None
    Returns:
        None
    """
    print('*' * 100)
    print('\t\t\t\t抖音App视频下载小助手')
    print('\t\t\t\t作者:yufei@www.Goodid.com')
    print('*' * 100)  

if __name__ == '__main__':
    hello()
    start_time = time.time()    # 开始时间
    user_id = input('请输入抖音用户UID:')
    user_id = user_id.strip() if user_id else '98554035339'

    save_dir = input('保存路径 (例如"E:/Download/", 默认"./Download/"):')
    save_dir = save_dir if save_dir else "./Download/"    
    num = run(user_id,save_dir)
    
    print('结束...')
    end_time = time.time()  # 结束时间
    print("一共下载了%s个抖音视频,总耗时%.2f秒,大约%.2f分钟" % (num, end_time - start_time, int(end_time - start_time) / 60))
    exit()
    
    