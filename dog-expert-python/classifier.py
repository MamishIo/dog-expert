from keras.applications.resnet import ResNet50, preprocess_input, decode_predictions
from keras.preprocessing.image import load_img, img_to_array
from numpy import expand_dims
from io import BytesIO
from PIL import Image as pil_image

class Classifier():

    model = ResNet50(weights='imagenet_weights.h5')

    def classify(self, data: bytes) -> dict:
        buffer = BytesIO(data)
        original_image = load_img(buffer, target_size=(224,224))
        numpy_image = img_to_array(original_image)
        image_batch = expand_dims(numpy_image,axis=0)
        processed_image = preprocess_input(image_batch)
        predictions = self.model.predict(processed_image)
        prediction_classes = decode_predictions(predictions,top=1000) # Always get all 1000 classes
        return self.pack_response(prediction_classes)

    # Classifier data is unlabelled and weirdly nested, so repack and label it to be easier to understand, e.g:
    # [[["n02106662", "German_shepherd", "0.99950993"], ["n02105162", "malinois", "0.0002614687"]]]
    def pack_response(self, predictions: list) -> dict:
        actual_predictions_list = predictions[0] # Unwrap outermost list, not sure why we even have that
        labelled_data = [self.pack_element(e) for e in actual_predictions_list]
        border_collie_prediction = [d for d in labelled_data if d['sequenceNumber'] == 'n02106166'][0]
        border_collie_index = labelled_data.index(border_collie_prediction)
        return {
            'borderColliePrediction': border_collie_prediction,
            'borderCollieIndex': border_collie_index,
            'predictions': labelled_data
        }

    def pack_element(self, item: list) -> dict:
        return {
            'sequenceNumber': item[0],
            'imageNetClassName': item[1],
            'confidence': item[2]
        }
