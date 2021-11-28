import pip, tempfile, random
tempfile._Random = lambda: random.Random('fixed-seed-for-deterministic-pip-install')
pip.main(['install', '-t', '.', '-r', 'python/requirements.txt'])