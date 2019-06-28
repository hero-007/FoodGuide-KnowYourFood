from PIL import Image
import requests
from io import BytesIO
import torchvision.transforms as transforms
import torch
import json
from commons import get_model

food_name = {}

with open('foodlables.txt') as f:
    for line in f:
        key, val = line.rstrip("\n").split('.')
        food_name[int(key)] = val 

model = get_model()

def convert_url_image_to_tensor(image):
    # This takes in an image and converts it into a tensor
    my_transforms = transforms.Compose([transforms.Resize(255),
                                        transforms.CenterCrop(224),
                                        transforms.ToTensor(),
                                        transforms.Normalize(
                                            [0.485,0.456,0.406],
                                            [0.229,0.224,0.225]
                                        )])
    return my_transforms(image).unsqueeze(0)

def get_food_name_api(image_tensor):
    outputs = model.forward(image_tensor)
    ps = torch.exp(outputs)
    _, prediction = ps.max(1)
    category = prediction.item()
    print(prediction,"----------")
    foodname = food_name[category+1]
    return foodname, category+1