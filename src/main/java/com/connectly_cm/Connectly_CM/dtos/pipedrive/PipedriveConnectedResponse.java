package com.connectly_cm.Connectly_CM.dtos.pipedrive;

import com.connectly_cm.Connectly_CM.models.pipedrive.CrmSettings;

public class PipedriveConnectedResponse {
    private boolean isPipedriveConnected;
    private CrmSettings crmSettings;
    private int responseCode;

    public static class PipedrivePersonsRequestBody {
        private int filter_id;
        private String first_char;
        private int start;
        private int limit;
        private String sort;

        public int getFilter_id() {
            return filter_id;
        }

        public void setFilter_id(int filter_id) {
            this.filter_id = filter_id;
        }

        public String getFirst_char() {
            return first_char;
        }

        public void setFirst_char(String first_char) {
            this.first_char = first_char;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }


    }
}
