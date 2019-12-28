# unidbgweb
unidbg的服务化

1. 将cn文件放在maven本地仓库、需要git下载unigdb，并install到maven里面
2. 项目启动需要加上VM options: -Djava.library.path=prebuilt/os -Djna.library.path=prebuilt/os
                  Where os may: linux64, win32, win64, osx64
3. 项目内置毒、酷安、小红书、马蜂窝app的相关so和用法
