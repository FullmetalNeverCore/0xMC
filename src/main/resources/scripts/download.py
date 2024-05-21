import os
import minecraft_launcher_lib
import psutil
import json
import argparse

current_max = 0

def set_status(status):
    print(status)

def set_progress(progress):
    if current_max != 0:
        print(f"{progress}/{current_max}")

def set_max(new_max):
    global current_max
    current_max = new_max

if __name__ == "__main__":
        parser = argparse.ArgumentParser()
        parser.add_argument('arg1', type=str, help='path to root.')
        script_dir = parser.parse_args().arg1
        settings_path = os.path.join(script_dir, "settings.json")
        settings = json.load(open(settings_path))

        minecraft_directory = minecraft_launcher_lib.utils.get_minecraft_directory()

        callback = {
            "setStatus": set_status,
            "setProgress": set_progress,
            "setMax": set_max
        }

        print(f"Preparing to download {settings['selected-version']}")
        print(f"Destination {settings['Minecraft-home']}")

        print("START")

        minecraft_launcher_lib.install.install_minecraft_version(settings["selected-version"], settings["Minecraft-home"], callback=callback)
        print(True)

