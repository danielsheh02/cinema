const getBaseUrl = () => {  
    const { hostname } = window.location;
    return `http://${hostname}:30099`;
};
  
const config = {
    API_URL: getBaseUrl(),
};
  
export default config;
  