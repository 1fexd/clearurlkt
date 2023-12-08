import os
import sys
import time
from datetime import datetime

from fwutil.FileWriter import open_file

git_hash = sys.argv[1]
now = int(time.time() * 1000.0)

cwd = os.getcwd()
gson_kt_extensions_dir = os.path.abspath(os.path.join(cwd, os.pardir))
source_path = os.path.join(gson_kt_extensions_dir, "kotlin")

with open_file(os.path.join(source_path, "ClearURLMetadata.kt")) as fw:
    fw.write_multiline(f"""
        object ClearURLsMetadata {{
            const val gitHash = "{git_hash}"
            const val fetchedAt = {now}L
        }}
    
    """)
