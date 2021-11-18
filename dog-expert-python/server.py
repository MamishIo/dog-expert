import base64, json
from http.server import ThreadingHTTPServer, BaseHTTPRequestHandler
from classifier import Classifier

def main():
    server_address = ('localhost', 8081)
    httpd = ThreadingHTTPServer(server_address, RequestHandler)
    httpd.serve_forever()

class RequestHandler(BaseHTTPRequestHandler):

    classifier = Classifier()

    def do_POST(self):
        request_json = json.load(self.rfile)
        image_raw_data = request_json['ImageData']
        image_data = base64.b64decode(image_raw_data)
        classifier_response = classifier.classify(image_data)
        self.wfile.write(classifier_response.encode('utf-8'))

if __name__ == "__main__":
    main()