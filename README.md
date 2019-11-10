# ubuntu环境下配置蓝牙环境
sudo apt-get install libbluetooth-dev python-pip libglib2.0-dev
sudo pip install bluepy
sudo pip install pybluez
#ubuntu下蓝牙设备查看
系统：Ubuntu 14.04

蓝牙：绿联USB2.0蓝牙适配器（型号：CM109；芯片：CSR8510）

一、蓝牙识别：

　　1、插入硬件，打开终端，查看是否检测到设备：

$ lsusb
Bus 001 Device 003: ID 0a12:0001 Cambridge Silicon Radio, Ltd Bluetooth Dongle (HCI mode)

　　2、查看是否识别为蓝牙模块：

$ hciconfig -a
hci0:    Type: BR/EDR    Bus: USB
            ...

　　3、查看蓝牙模块的地址；如果不显示蓝牙模块及其地址，则需要通过rfkill list命令查看hci0是否blocked，使用rfkill unblock 0（rfkill list显示的hci0的序号）即可启用蓝牙模块（hci0）。

$ hcitool dev
Devices:
    hci0    00:1A:7D:DA:71:11

　　4、激活蓝牙模块：

$ sudo hciconfig hci0 up

　　激活蓝牙模块之后，即可通过手机蓝牙正常连接。如果手机搜索不到该蓝牙模块，可能因为Ubuntu下蓝牙模块默认为不可见，需要在Ubuntu上方工具栏中点击蓝牙图标，设置Visible ON即可（暂时没有找到Ubuntu下设置蓝牙可见性的终端命令）。

　　5、此次测试设置蓝牙模块为服务端且不需要配对码：

$ hciconfig hci0 noauth

　　6、hciconfig和hcitool（BlueZ提供的工具，BlueZ是多数Linux发行版的默认蓝牙协议栈）可以实现搜索、连接等功能，此处主要希望通过编程控制蓝牙模块，故对此暂时不做深究。

二、PyBluez安装：

　　1、下载并解压PyBluez-0.22，进入PyBluez-0.22目录；（https://github.com/karulis/pybluez）

　　2、安装PyBluez-0.22：

$ sudo python setup.py install

　　　　出现问题：

In file included from bluez/btmodule.c:20:0:
    bluez/btmode.h:5:33: fatal error: bluetooth/bluetooth.h: No such file or directory
    #include <bluetooth/bluetooth.h>

　　　　解决问题：安装libbluetooth-dev：

$ sudo apt-get install libbluetooth-dev

三、PyBluez测试（参考PyBluez自带example实现）：

　　1、查询设备列表：

import bluetooth

nearby_devices = bluetooth.discover_devices(lookup_names=True)for addr, name in nearby_devices:
    print("  %s - %s" % (addr, name))

　　2、查询设备服务：
复制代码

import bluetooth

nearby_devices = bluetooth.discover_devices(lookup_names=True)for addr, name in nearby_devices:
    print("  %s - %s" % (addr, name))

    services = bluetooth.find_service(address=addr)
    for svc in services:
        print("Service Name: %s"    % svc["name"])
        print("    Host:        %s" % svc["host"])
        print("    Description: %s" % svc["description"])
        print("    Provided By: %s" % svc["provider"])
        print("    Protocol:    %s" % svc["protocol"])
        print("    channel/PSM: %s" % svc["port"])
        print("    svc classes: %s "% svc["service-classes"])
        print("    profiles:    %s "% svc["profiles"])
        print("    service id:  %s "% svc["service-id"])
        print("")

复制代码

　　3、RFCOMM：

　　　　蓝牙串口服务端：
复制代码

import bluetooth

if __name__ == "__main__":
    print("looking for nearby devices...")
    nearby_devices = bluetooth.discover_devices(lookup_names=True)
    for addr, name in nearby_devices:
        print("%s %s" % (addr, name))

    server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    server_sock.bind(("", bluetooth.PORT_ANY))
    server_sock.listen(1)
    
    port = server_sock.getsockname()[1]
    
    uuid = "00001101-0000-1000-8000-00805f9b34fb"
    bluetooth.advertise_service(server_sock, "SampleServer", service_id=uuid)
    
    print("Waiting for connection on RFCOMM channel %d" % port)
    client_sock, client_info = server_sock.accept()
    print(client_info)
    
    try:
        while True:
            data = client_sock.recv(1024)
            if len(data) == 0:
                break
            print("received [%s]" % data)
    except IOError:
        pass
    
    client_sock.close()
    server_sock.close()
    print("this test is done!")
    

复制代码

　　　　出现问题：Segmentation Fault（参考rfcomm-server.py给定advertise_service方法6个参数时）

　　　　解决问题：首先了解错误原因——内存访问越界，说明PyBluez封装BlueZ存在着一些bug；然后定位错误出现位置：advertise_service；在此之后查看PyBluez源码——bluetooth文件夹下的bluez.py文件中advertise_service的实现，发现该方法最少只需要前三个参数即可，去除多余参数后，运行成功。

　　　　总结问题：这种多余参数出现bug的情况非常典型，在程序实现的初期，通常是针对必要参数进行处理而忽视了多余的/扩展的/辅助的参数，而在一般测试过程中通常也很少能够检测到这种参数问题，因此，实际应用时，减少多余参数是绕过bug的一种很实用的方法。

 

　　最后，通过手机蓝牙串口App连接蓝牙串口服务端，成功实现信息传递。（^_^）

