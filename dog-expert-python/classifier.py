from keras.applications.resnet import ResNet50, preprocess_input, decode_predictions
from keras.preprocessing.image import load_img

class Classifier():
    def classify(self, data: bytes) -> str:
        buffer = BytesIO(data)
        return "Hello!"
