from flask import Flask, render_template,request
from commons import get_tensor, get_food_information
from flask import jsonify
import urllib.request
from PIL import Image
from api_functions import convert_url_image_to_tensor, get_food_name_api
from food_details import getFoodDetail
import os
import time 
import random

app = Flask(__name__)

@app.route('/api',methods=['POST','GET'])
def api():
    # This function will get called if someone tries to interact with api of the application

    # sample request - http://127.0.0.1:5000/api?url=https://images.pexels.com/photos/96938/pexels-photo-96938.jpeg
    
    image_url = request.args.get('url')
    print("image url =",image_url)

    ## use image_url to download and save the image locally
    a = int(time.time())+random.randint(1,10000001)

    image_name  = str(a)+"food_to_predict.jpg"

    urllib.request.urlretrieve(image_url,image_name)
    ## open the downloaded image using PIL
    image = Image.open(image_name)
    # convert this image to tensor 
    image_tensor = convert_url_image_to_tensor(image)
    # using image_tensor get the bird_name and its category
    food_name, category = get_food_name_api(image_tensor=image_tensor)
    
    # binding food_name and category into a dictionary
    result_dict = getFoodDetail(food_name)
    result_dict["category"] = str(category)
    # remove the image file after inference is done 
    os.remove(image_name)
    # final return a json file
    return jsonify(result_dict)

if __name__ == '__main__':
    app.run()
