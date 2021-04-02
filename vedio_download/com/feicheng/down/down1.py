# -*- coding: UTF-8 -*-

import os
import sys
import uuid


args=sys.argv
output_path="F://1_vedio"
output_filename=""
url=""
if len(args)==1:
    print("请至少输入一个视频地址")
    sys.exit(1)
elif len(args)==2:
    url=args[1]
    output_filename=str(uuid.uuid4()).replace("-","")
elif len(args)==3:
    output_path=args[1]
    url=args[2]
    output_filename=str(uuid.uuid4()).replace("-","")
elif len(args)==4:
    output_path=args[1]
    output_filename=args[2]
    url=args[3]


print("输出目录：",output_path)
print("输出文件名:",output_filename)
print("下载地址:",url)

print("###################################################")
print("#################准备下载###################")
print("###################################################")
cmd="you-get -o {PATH} -O {FILENAME} {URL}".format(PATH=output_path,FILENAME=output_filename,URL=url)
os.system(cmd)







