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
        try:
            content_len = int(self.headers.get('content-length', 0))
            request_json = json.loads(self.rfile.read(content_len))
            image_raw_data = request_json['image_data']
            image_data = base64.b64decode(image_raw_data)
            classifier_response = self.classifier.classify(image_data)
            print('Got classifier response: ' + str(classifier_response))
            response = (200, {'data': classifier_response})
        except Exception as e:
            print('Got exception: ' + repr(e))
            response = (500, {'error': repr(e)})
        # Send data or error response
        self.send_response(response[0])
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        response_json = json.dumps(response[1], default=str)
        print('Final response JSON payload: ' + response_json)
        self.wfile.write(response_json.encode('utf-8'))


if __name__ == "__main__":
    main()
