# -*- coding: UTF-8 -*-
from com.feicheng.utils.FileUtils import *
import subprocess



cur_path=os.path.dirname(__file__)
root_path=cur_path[:cur_path.find("vedio_download")+len("vedio_download")]
resource_file=root_path+"/resource/girefreind"
output_path="F:/1_vedio/2"
fullpath=output_path

FileUtils().mkdir(fullpath)

with open(resource_file,"r",encoding="utf-8") as f:
    for line in f.readlines():
        print(line.strip().split(","))
        output_filename=line.strip().split(",")[0]
        url=line.strip().split(",")[1]
        print("系统准备下载电影:"+output_filename)
        cmd="you-get -o {PATH} -O {FILENAME} {URL}".format(PATH=output_path,FILENAME=output_filename,URL=url)
        os.system(cmd)
        print(cmd)


