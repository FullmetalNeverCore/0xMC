import os
import subprocess
import minecraft_launcher_lib
import uuid
import json
import platform
import psutil
import argparse




mem = psutil.virtual_memory()
mc_dir = r"./minecraft" 
curr_dir = os.getcwd()


def get_size(bytes, suffix="B"):
        fc = 1024
        for unit in ["", "K", "M", "G", "T", "P"]:
            if bytes < fc:
                return f"{bytes:.2f}{unit}{suffix}"
            bytes /= fc


default_settings = {
            "User-info": [
                {
                    "username": None,
                    "AUTH_TYPE": "cracked",
                    "UUID": None
                }
            ],
            "PC-info": [
                {
                    "OS": platform.platform(),
                    "Total-Ram": f"{get_size(mem.total)}",
                }
            ],
            "Minecraft-home": mc_dir,
            "current_dir": curr_dir,
            "selected-version": None,
            "allocated_ram": None,
            "jvm-args": None,
            "executablePath": "java",
        }


def path_file():
    with open(r"{}/path.json".format(curr_dir), "w") as js_set:
        json.dump("{}/settings.json".format(curr_dir),js_set)    


def write_settings(stngs,curr_dir=os.getcwd()):
        with open(r"{}/settings.json".format(curr_dir), "w") as js_set:
            json.dump(stngs, js_set, indent=4)


def load_settings(curr_dir=os.getcwd()):
        try:
            with open(r"{}/settings.json".format(curr_dir), "r") as jread:
                data = json.load(jread)
                return data
        except FileNotFoundError:
            print("Settings json is absent,creating a new one..")
            write_settings(default_settings)
            return default_settings

def creating_folder_struct():
        if os.path.exists(r"{}/.minecraft".format(curr_dir)):
            print("Existing minecraft installation, checking for versions...")
        else:
            os.mkdir(".minecraft")
            os.chdir(".minecraft")
            os.mkdir("versions")

def generate_cracked_uid():
        settings = load_settings()
        if settings["User-info"][0]["UUID"] is None:
            settings["User-info"][0]["UUID"] = uuid.uuid4().hex
            write_settings(settings)

def create_args():
        settings = load_settings()
        if settings["allocated_ram"] is None:
            raise ValueError("Allocated RAM is not set in settings.")
        # Xmx maximum heap  Xms initial heap 
        settings["jvm-args"] = [f"-Xmx{int(settings['allocated_ram'])}M", 
                                "-Xms128M",
                                "-XX:+UseG1GC",
                                "-Dsun.rmi.dgc.server.gcInterval=2147483646",
                                "-XX:+UnlockExperimentalVMOptions",
                                "-XX:G1NewSizePercent=20",
                                "-XX:G1ReservePercent=20",
                                "-XX:MaxGCPauseMillis=25",
                                "-XX:G1HeapRegionSize=32M"]
        write_settings(settings)


def rungame():
    settings = load_settings()
    options = {
        "username" : settings["User-info"][0]["username"],
        "uuid" : settings["User-info"][0]["UUID"],
        "token" : ""
    }
    print(f"Startup_options {options}")

    minecraft_command = minecraft_launcher_lib.command.get_minecraft_command(settings["selected-version"], settings["Minecraft-home"], options)
    print(f"Launching {settings['selected-version']}....")
    subprocess.call(minecraft_command)

if __name__ == "__main__":
        parser = argparse.ArgumentParser()
        parser.add_argument('arg1', type=str, help='a username')
        parser.add_argument('arg2', type=str, help='game version')
        parser.add_argument('arg3', type=str, help='allocram')
        parser.add_argument('arg4', type=str, help='action')
        args = parser.parse_args()

        #generation of settings file
        default_settings["User-info"][0]["username"] = args.arg1
        default_settings["selected-version"] = args.arg2
        default_settings["allocated_ram"] = args.arg3

        write_settings(default_settings)
        print(r"{}/settings.json".format(curr_dir))
        path_file()

        create_args()
        generate_cracked_uid()

        settings = load_settings()

        print(settings)

        if args.arg4 == "start":
            print("Starting Minecraft...")
            print(f"Machine: {settings['PC-info'][0]['OS']}")

            rungame()


