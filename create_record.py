"""
Usage:
  # From tensorflow/models/
  # Create train data:
  python generate_tfrecord.py --input_csv=data/train_labels.csv  --output_tfrecord=train.record
  # Create test data:
  python generate_tfrecord.py --input_csv=data/test_labels.csv  --output_tfrecord=test.record
"""
from __future__ import division
from __future__ import print_function
from __future__ import absolute_import

import os
import io
import pandas as pd
import tensorflow as tf

from PIL import Image
from object_detection.utils import dataset_util
from collections import namedtuple, OrderedDict
import argparse


# TO-DO replace this with label map
def class_text_to_int(row_label):
    if row_label == 'ball':
      return 1
    elif row_label == 'bench':
      return 2
    elif row_label == 'bicycle':
      return 3
    elif row_label == 'boat':
      return 4
    elif row_label == 'cake':
      return 5
    elif row_label == 'can':
      return 6
    elif row_label == 'car':
      return 7
    elif row_label == 'card':
      return 8
    elif row_label == 'chair':
      return 9
    elif row_label == 'clock':
      return 10
    elif row_label == 'door':
      return 11
    elif row_label == 'house':
      return 12
    elif row_label == 'motorcycle':
      return 13
    elif row_label == 'mug':
      return 14
    elif row_label == 'plate':
      return 15
    elif row_label == 'present':
      return 16
    elif row_label == 'sign':
      return 17
    elif row_label == 'tree':
      return 18
    elif row_label == 'window':
      return 19
    elif row_label == 'balloon':
      return 20
    else:
        None


def split(df, group):
    data = namedtuple('data', ['filename', 'object'])
    gb = df.groupby(group)
    return [data(filename, gb.get_group(x)) for filename, x in zip(gb.groups.keys(), gb.groups)]


def create_tf_example(group, path):
    with tf.gfile.GFile(os.path.join(path, '{}'.format(group.filename)), 'rb') as fid:
        encoded_jpg = fid.read()
    encoded_jpg_io = io.BytesIO(encoded_jpg)
    image = Image.open(encoded_jpg_io)
    width, height = image.size

    filename = group.filename.encode('utf8')
    image_format = b'jpg'
    xmins = []
    xmaxs = []
    ymins = []
    ymaxs = []
    classes_text = []
    classes = []

    for index, row in group.object.iterrows():
        xmins.append(row['xmin'] / width)
        xmaxs.append(row['xmax'] / width)
        ymins.append(row['ymin'] / height)
        ymaxs.append(row['ymax'] / height)
        classes_text.append(row['class'].encode('utf8'))
        classes.append(class_text_to_int(row['class']))

    tf_example = tf.train.Example(features=tf.train.Features(feature={
        'image/height': dataset_util.int64_feature(height),
        'image/width': dataset_util.int64_feature(width),
        'image/filename': dataset_util.bytes_feature(filename),
        'image/source_id': dataset_util.bytes_feature(filename),
        'image/encoded': dataset_util.bytes_feature(encoded_jpg),
        'image/format': dataset_util.bytes_feature(image_format),
        'image/object/bbox/xmin': dataset_util.float_list_feature(xmins),
        'image/object/bbox/xmax': dataset_util.float_list_feature(xmaxs),
        'image/object/bbox/ymin': dataset_util.float_list_feature(ymins),
        'image/object/bbox/ymax': dataset_util.float_list_feature(ymaxs),
        'image/object/class/text': dataset_util.bytes_list_feature(classes_text),
        'image/object/class/label': dataset_util.int64_list_feature(classes),
    }))
    return tf_example


def main(_):

     # Taking command line arguments from users
    parser = argparse.ArgumentParser()
    parser.add_argument('-in', '--input_csv', help='define the input xml file', type=str, required=True)
    parser.add_argument('-out', '--output_tfrecord', help='define the output file ', type=str, required=True)
    args = parser.parse_args()
    writer = tf.python_io.TFRecordWriter(args.output_tfrecord)
    path = os.path.join(os.getcwd(), 'images')
    examples = pd.read_csv(args.input_csv)
    grouped = split(examples, 'filename')
    for group in grouped:
        tf_example = create_tf_example(group, path)
        writer.write(tf_example.SerializeToString())

    writer.close()
    output_path = os.path.join(os.getcwd(), args.output_tfrecord)
    print('Successfully created the TFRecords: {}'.format(output_path))


if __name__ == '__main__':
    tf.app.run()