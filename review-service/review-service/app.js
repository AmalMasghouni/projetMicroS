const express = require('express');
const bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');
const { Eureka } = require('eureka-js-client');
const os = require('os');
const axios = require('axios');

const app = express();
const port = process.env.PORT || 3002;

app.use(bodyParser.json());

const reviews = [];

app.get('/api/reviews', (req, res) => {
  const { productId } = req.query;
  if (productId) {
    res.json(reviews.filter(r => r.productId === productId));
  } else {
    res.json(reviews);
  }
});

// 🔐 Fonction pour récupérer un token JWT de Keycloak (client_credentials)
async function getAccessToken() {
  const tokenUrl = 'http://keycloak:8080/realms/microservices-realm/protocol/openid-connect/token';
  const clientId = 'internal-client';
  const clientSecret = '5wZWtc8aDpEqegweiaQLgW25xK3UylZT'; // 🔁 À remplacer !

  const params = new URLSearchParams();
  params.append('grant_type', 'client_credentials');
  params.append('client_id', clientId);
  params.append('client_secret', clientSecret);

  const response = await axios.post(tokenUrl, params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });

  return response.data.access_token;
}

app.post('/api/reviews', async (req, res) => {
  const { productId, userId, rating, comment } = req.body;

  if (!productId || !userId || !rating) {
    return res.status(400).json({ error: 'productId, userId et rating requis' });
  }

  try {
    const token = await getAccessToken();

    const productResponse = await axios.get(
      `http://product-service:8081/api/product/${productId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!productResponse.data) {
      return res.status(404).json({ error: 'Produit non trouvé' });
    }
  } catch (error) {
    console.error('Erreur lors de la vérification du produit:', error.message);
    return res.status(500).json({ error: 'Erreur lors de la vérification du produit' });
  }

  const review = {
    id: uuidv4(),
    productId,
    userId,
    rating,
    comment: comment || '',
    date: new Date().toISOString(),
  };

  reviews.push(review);
  res.status(201).json(review);
});

app.get('/health', (req, res) => {
  res.json({ status: 'review-service up' });
});

// Fonction pour récupérer l'IP du container
function getIpAddress() {
  const interfaces = os.networkInterfaces();
  for (const ifaceName of Object.keys(interfaces)) {
    for (const iface of interfaces[ifaceName]) {
      if (iface.family === 'IPv4' && !iface.internal) {
        return iface.address;
      }
    }
  }
  return '127.0.0.1';
}

const ipAddr = getIpAddress();

const eurekaClient = new Eureka({
  instance: {
    app: 'REVIEW-SERVICE',
    instanceId: `review-service:${port}`,
    hostName: 'review-service',
    ipAddr: ipAddr,
    statusPageUrl: `http://review-service:${port}/health`,
    healthCheckUrl: `http://review-service:${port}/health`,
    port: {
      '$': port,
      '@enabled': true,
    },
    vipAddress: 'review-service',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    serviceUrls: {
      default: ['http://discovery-server:8761/eureka/apps/'],
    },
    maxRetries: 10,
    requestRetryDelay: 5000,
    logger: console,
  },
});

eurekaClient.start(error => {
  if (error) {
    console.error('Erreur enregistrement Eureka :', error);
  } else {
    console.log('review-service enregistré avec Eureka');
  }
});

app.listen(port, () => {
  console.log(`review-service listening on port ${port}`);
});
