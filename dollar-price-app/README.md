# Dollar Price Tracker

A full-stack application that tracks USD to CAD exchange rates using real-time data from external APIs. The application consists of a Spring Boot backend and an Angular frontend with interactive charts.

## Features

- **Real-time Data**: Fetches USD to CAD exchange rates from api.frankfurter.app
- **Scheduled Updates**: Automatically updates prices daily at midnight
- **Interactive Charts**: Beautiful line charts showing 7-day price history
- **Mobile Responsive**: Optimized for all device sizes
- **Health Monitoring**: Built-in health check endpoints
- **Docker Support**: Complete containerization with Docker Compose

## Technology Stack

### Backend
- **Spring Boot 3.2.0**: Main framework
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database for development
- **Lombok**: Reduces boilerplate code
- **Maven**: Build tool

### Frontend
- **Angular 17**: Modern frontend framework
- **Chart.js**: Interactive charts
- **ng2-charts**: Angular wrapper for Chart.js
- **Responsive Design**: Mobile-first approach

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Docker and Docker Compose (optional)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd dollar-price-app
   ```

2. **Backend Setup**
   ```bash
   # Navigate to backend directory
   cd dollar-price-app
   
   # Build the application
   ./mvnw clean install
   
   # Run the application
   ./mvnw spring-boot:run
   ```

3. **Frontend Setup**
   ```bash
   # Navigate to frontend directory
   cd frontend
   
   # Install dependencies
   npm install
   
   # Start development server
   npm start
   ```

4. **Access the Application**
   - Backend API: http://localhost:8080
   - Frontend: http://localhost:4200
   - H2 Console: http://localhost:8080/h2-console

### Docker Deployment

1. **Build and run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

2. **Access the application**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080

## API Endpoints

### Health Check
- **GET** `/api/v1/health`
  - Returns: `{"status": "UP"}`

### Dollar Prices
- **GET** `/api/v1/prices`
  - Returns: Array of dollar prices from the last 7 days
  - Example response:
    ```json
    [
      {
        "id": 1,
        "price": 1.35,
        "timestamp": "2024-01-08T10:00:00Z"
      }
    ]
    ```

## Testing

### Backend Tests
```bash
# Run all tests
./mvnw test

# Run integration tests
./mvnw test -Dtest=DollarPriceControllerIT

# Run unit tests
./mvnw test -Dtest=CurrencyServiceTest
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Free Hosting Guide

### Backend Deployment (Heroku)

1. **Create Heroku Account**
   - Sign up at [heroku.com](https://heroku.com)

2. **Install Heroku CLI**
   ```bash
   # Download and install from https://devcenter.heroku.com/articles/heroku-cli
   ```

3. **Deploy Backend**
   ```bash
   # Login to Heroku
   heroku login
   
   # Create new app
   heroku create your-dollar-price-app
   
   # Add PostgreSQL addon
   heroku addons:create heroku-postgresql:mini
   
   # Deploy
   git push heroku main
   ```

4. **Configure Environment**
   ```bash
   # Set environment variables
   heroku config:set SPRING_PROFILES_ACTIVE=prod
   ```

### Frontend Deployment (Vercel)

1. **Create Vercel Account**
   - Sign up at [vercel.com](https://vercel.com)

2. **Deploy Frontend**
   ```bash
   # Install Vercel CLI
   npm i -g vercel
   
   # Navigate to frontend directory
   cd frontend
   
   # Deploy
   vercel --prod
   ```

3. **Update API URL**
   - Update the `baseUrl` in `src/app/services/api.service.ts` to point to your Heroku backend URL

### Alternative: Netlify Deployment

1. **Create Netlify Account**
   - Sign up at [netlify.com](https://netlify.com)

2. **Deploy Frontend**
   ```bash
   # Build the application
   cd frontend
   npm run build
   
   # Deploy to Netlify (drag and drop dist folder)
   ```

## Configuration

### Backend Configuration

The application uses different profiles for different environments:

- **Development**: Uses H2 in-memory database
- **Production**: Uses PostgreSQL (Heroku)
- **Docker**: Uses environment-specific settings

### Frontend Configuration

Update the API service URL in `src/app/services/api.service.ts`:

```typescript
private readonly baseUrl = 'https://your-backend-url.herokuapp.com/api/v1';
```

## Monitoring and Health Checks

### Backend Health
- Endpoint: `/api/v1/health`
- Docker health check configured
- Scheduled task monitoring

### Frontend Health
- Built-in error handling
- Loading states
- Retry mechanisms

## Security Considerations

- CORS configuration for frontend-backend communication
- Input validation on API endpoints
- Secure headers in nginx configuration
- Environment variable management

## Performance Optimization

### Backend
- Connection pooling
- Query optimization
- Caching strategies

### Frontend
- Lazy loading
- Bundle optimization
- CDN usage for static assets

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure backend CORS configuration matches frontend URL
   - Check environment variables

2. **Database Connection Issues**
   - Verify database URL in production
   - Check connection pool settings

3. **Chart Not Loading**
   - Verify API endpoint accessibility
   - Check browser console for errors

### Logs

```bash
# Backend logs
docker-compose logs backend

# Frontend logs
docker-compose logs frontend
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the API documentation 