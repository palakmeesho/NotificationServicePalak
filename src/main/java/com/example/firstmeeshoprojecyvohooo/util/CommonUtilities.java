package com.example.firstmeeshoprojecyvohooo.util;

import java.util.UUID;

public class CommonUtilities {
        public String generateUniqueRequestId()
       {
          return UUID.randomUUID().toString().replace("-", "");
       }
}
