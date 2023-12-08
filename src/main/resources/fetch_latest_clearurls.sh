wget https://raw.githubusercontent.com/ClearURLs/Rules/master/data.min.json -O clearurls.json
head_commit_hash=$(git ls-remote https://github.com/ClearURLs/rules | head -n 1 | awk '{print $1}')
python3 write_metadata.py "$head_commit_hash"
