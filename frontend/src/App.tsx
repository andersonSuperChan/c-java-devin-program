import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import { cn } from '@/lib/utils';
import StudentPage from './pages/StudentPage';
import BookPage from './pages/BookPage';
import LoanPage from './pages/LoanPage';
import QueryPage from './pages/QueryPage';
import { BookOpen, Users, ArrowLeftRight, Search, Home } from 'lucide-react';

function NavLink({ to, children, icon: Icon }: { to: string; children: React.ReactNode; icon: React.ComponentType<{ className?: string }> }) {
  const location = useLocation();
  const isActive = location.pathname === to;
  
  return (
    <Link
      to={to}
      className={cn(
        'flex items-center gap-2 px-4 py-2 rounded-lg transition-colors',
        isActive
          ? 'bg-primary text-primary-foreground'
          : 'hover:bg-muted'
      )}
    >
      <Icon className="w-5 h-5" />
      {children}
    </Link>
  );
}

function HomePage() {
  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-center">Library Management System</h1>
      <p className="text-center text-gray-600">Welcome to the Library Management System</p>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 max-w-2xl mx-auto">
        <Link
          to="/students"
          className="p-6 border rounded-lg hover:bg-muted transition-colors"
        >
          <Users className="w-8 h-8 mb-2 text-primary" />
          <h2 className="text-xl font-semibold">Student Management</h2>
          <p className="text-gray-600 text-sm">Add, edit, delete, and view students</p>
        </Link>
        
        <Link
          to="/books"
          className="p-6 border rounded-lg hover:bg-muted transition-colors"
        >
          <BookOpen className="w-8 h-8 mb-2 text-primary" />
          <h2 className="text-xl font-semibold">Book Management</h2>
          <p className="text-gray-600 text-sm">Add, edit, delete, and view books</p>
        </Link>
        
        <Link
          to="/loans"
          className="p-6 border rounded-lg hover:bg-muted transition-colors"
        >
          <ArrowLeftRight className="w-8 h-8 mb-2 text-primary" />
          <h2 className="text-xl font-semibold">Borrow / Return</h2>
          <p className="text-gray-600 text-sm">Borrow and return books</p>
        </Link>
        
        <Link
          to="/query"
          className="p-6 border rounded-lg hover:bg-muted transition-colors"
        >
          <Search className="w-8 h-8 mb-2 text-primary" />
          <h2 className="text-xl font-semibold">Query System</h2>
          <p className="text-gray-600 text-sm">Search books, students, history, and overdue</p>
        </Link>
      </div>
    </div>
  );
}

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background">
        <nav className="border-b bg-card">
          <div className="container mx-auto px-4 py-3">
            <div className="flex items-center justify-between">
              <Link to="/" className="text-xl font-bold flex items-center gap-2">
                <BookOpen className="w-6 h-6" />
                Library Management System
              </Link>
              <div className="flex gap-2">
                <NavLink to="/" icon={Home}>Home</NavLink>
                <NavLink to="/students" icon={Users}>Students</NavLink>
                <NavLink to="/books" icon={BookOpen}>Books</NavLink>
                <NavLink to="/loans" icon={ArrowLeftRight}>Loans</NavLink>
                <NavLink to="/query" icon={Search}>Query</NavLink>
              </div>
            </div>
          </div>
        </nav>
        
        <main className="container mx-auto px-4 py-6">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/students" element={<StudentPage />} />
            <Route path="/books" element={<BookPage />} />
            <Route path="/loans" element={<LoanPage />} />
            <Route path="/query" element={<QueryPage />} />
          </Routes>
        </main>
        
        <footer className="border-t bg-card mt-auto">
          <div className="container mx-auto px-4 py-4 text-center text-sm text-gray-600">
            Library Management System - Converted from C to B/S Architecture with Java Backend
          </div>
        </footer>
      </div>
    </Router>
  );
}

export default App
