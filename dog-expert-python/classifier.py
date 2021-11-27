from keras.applications.resnet import ResNet50, preprocess_input, decode_predictions
from keras.preprocessing.image import load_img, img_to_array
from numpy import expand_dims
from io import BytesIO
from PIL import Image as pil_image

class Classifier():

    model = ResNet50(weights='imagenet')

    def classify(self, data: bytes) -> dict:
        buffer = BytesIO(data)
        original_image = load_img(buffer, target_size=(224,224))
        numpy_image = img_to_array(original_image)
        image_batch = expand_dims(numpy_image,axis=0)
        processed_image = preprocess_input(image_batch)
        predictions = self.model.predict(processed_image)
        predictions_readable = decode_predictions(predictions,top=5)
        return {'predictions': predictions_readable}
