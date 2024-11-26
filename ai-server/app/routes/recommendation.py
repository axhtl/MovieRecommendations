from flask import Blueprint, request, jsonify


recommendation_api = Blueprint('recommendation', __name__)

@recommendation_api.route('/recommend', methods=['POST'])
def recommend():
    pass