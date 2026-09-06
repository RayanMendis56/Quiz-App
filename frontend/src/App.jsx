import WelcomeScreen from './pages/WelcomeScreen'; 
import './App.css';

function App() {
  const handleStartQuiz = (data) => { console.log('Quiz start data:', data); }; 
  return ( <div className="App"> <WelcomeScreen onStartQuiz={handleStartQuiz} /> </div> );
}

export default App;
