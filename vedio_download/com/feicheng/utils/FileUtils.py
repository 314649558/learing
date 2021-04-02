# -*- coding: UTF-8 -*-
import os

class FileUtils():




    """文件管理工具类"""

    def mkdir(self,path):
        if not os.path.exists(path):
            os.makedirs(path)
            print(path+":目录创建成功")
        else:
            print(path+":目录已经存在")


    def deleteDir(self,path):
        if not os.path.exists(path):
            print(path+":目录不存在")
        else:
            os.removedirs(path)
            print(path+":目录已经删除")



    def get_project_root_dir(self):
        PROJECT_NAME="vedio_download"
        cur_path=os.path.dirname(__file__)
        return cur_path[:cur_path.find(PROJECT_NAME)+len(PROJECT_NAME)]



    def get_down_path(self):
        resource_file=FileUtils.get_project_root_dir()


if __name__ == '__main__':
    print(FileUtils().get_project_root_dir())
