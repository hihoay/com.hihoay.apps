# import os
# from PIL import Image
# import cv2
# path = './'
#
# files = []
# # r=root, d=directories, f = files
# for r, d, f in os.walk(path):
#     for file in f:
#         if '.png' in file:
#             files.append(os.path.join(r, file))
# max = 524288
# for f in files:
#     if os.stat(f).st_size > max:
#         try:
#             print(f, os.stat(f).st_size)
#             im = Image.open(f)
#             rgb_im = im.convert('RGB')
#             rgb_im.save(str(f).repalce('png','jpg'))
#             print(im.mode)
#             os.remove(f)
#         except:
#             pass


import cv2
import os
# !/usr/bin/env python
from glob import glob

pngs = glob('../**/*.png', recursive=True)
max = 214572
for j in pngs:
    if os.stat(j).st_size > max and 'app' in str(j) and 'res' in str(j):
        print(j)
        img = cv2.imread(j)
        cv2.imwrite(j[:-3] + 'jpg', img)
        os.remove(j)
