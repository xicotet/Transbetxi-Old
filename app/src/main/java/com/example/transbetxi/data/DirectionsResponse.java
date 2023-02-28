package com.example.transbetxi.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DirectionsResponse {

    @SerializedName("features")
    @Expose
    private List<Feature> features = null;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public static class Feature {

        @SerializedName("geometry")
        @Expose
        private Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public static class Geometry {

            @SerializedName("coordinates")
            @Expose
            private List<List<Double>> coordinates = null;

            public List<List<Double>> getCoordinates() {
                return coordinates;
            }

            public void setCoordinates(List<List<Double>> coordinates) {
                this.coordinates = coordinates;
            }
        }
    }
}
