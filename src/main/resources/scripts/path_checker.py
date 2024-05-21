import os 
import argparse


def check_path(version):
    if os.path.exists("./minecraft"):
        if os.path.exists(f"./minecraft/versions/{version}"):
            return True
        else:
            return False
    else:
        return False


if __name__ == "__main__": 
    parser = argparse.ArgumentParser()
    parser.add_argument('arg1', type=str, help='game version')
    args = parser.parse_args()
    print(check_path(args.arg1))