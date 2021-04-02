# -*- coding: UTF-8 -*-
import os
from com.feicheng.utils.FileUtils import *


fileUtils=FileUtils()

cur_path=os.path.dirname(__file__)
root_path=cur_path[:cur_path.find("vedio_download")+len("vedio_download")]
resource_file=root_path+"/resource/zuma.txt"

output_path="F:/1_vedio/竹马钢琴师"
cookies=root_path+"/resource/cookies.txt"
fullpath=output_path

fileUtils.mkdir(fullpath)



with open(resource_file,"r",encoding="utf-8") as f:
    for line in f.readlines():
        line_arr=line.strip().split(",")
        output_filename=""
        url=""
        if len(line_arr)==3:
            status=int(line_arr[0])
            if status==0:
                continue
            else:
                output_filename=line_arr[1]
                url=line_arr[2]
        else:
            output_filename=line_arr[0]
            url=line_arr[1]
        print("系统准备下载电影:"+output_filename)
        print("URL:"+url)
        cmd="you-get -o {PATH} -O {FILENAME} -c {COOKIES} {URL}".format(PATH=output_path,FILENAME=output_filename,COOKIES=cookies,URL=url)
        print(cmd)
        os.system(cmd)
        print(output_filename+"已经下载完成咯")



