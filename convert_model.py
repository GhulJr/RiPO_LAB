import tensorflow as tf
saved_model_dir = './model/'

input_arrays = ["input"]
output_arrays = ["MobilenetV1/Predictions/Softmax"]

converter =  tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)#from_frozen_graph(saved_model_dir, input_arrays, output_arrays)
tflite_model = converter.convert()
open("converted_model.tflite", "wb").write(tflite_model)

